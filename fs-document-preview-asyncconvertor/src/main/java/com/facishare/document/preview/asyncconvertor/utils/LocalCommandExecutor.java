package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.asyncconvertor.model.ExecuteResult;
import com.facishare.document.preview.asyncconvertor.model.StreamGobbler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.*;

/**
 * Created by liuq on 2017/4/21.
 */
@Slf4j
public class LocalCommandExecutor {
    static ExecutorService pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            3L, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public ExecuteResult executeCommand(String[] command, long timeout) {
        Process process = null;
        InputStream pIn = null;
        InputStream pErr = null;
        StreamGobbler outputGobbler = null;
        StreamGobbler errorGobbler = null;
        Future<Integer> executeFuture = null;
        try {
            process = Runtime.getRuntime().exec(command);
            final Process p = process;
            p.getOutputStream().close();
            pIn = process.getInputStream();
            outputGobbler = new StreamGobbler(pIn, "OUTPUT");
            outputGobbler.start();
            pErr = process.getErrorStream();
            errorGobbler = new StreamGobbler(pErr, "ERROR");
            errorGobbler.start();
            Callable<Integer> call = () -> {
                p.waitFor();
                return p.exitValue();
            };
            executeFuture = pool.submit(call);
            int exitCode = executeFuture.get(timeout, TimeUnit.MILLISECONDS);
            return new ExecuteResult(exitCode, outputGobbler.getContent());
        } catch (IOException ex) {
            String errorMessage = "The command [" + command + "] execute failed.";
            log.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (TimeoutException ex) {
            String errorMessage = "The command [" + command + "] timed out.";
            log.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (ExecutionException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an execution error.";
            log.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (InterruptedException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an interrupted error.";
            log.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } finally {
            if (executeFuture != null) {
                try {
                    executeFuture.cancel(true);
                } catch (Exception ignore) {
                }
            }
            if (pIn != null) {
                this.closeQuietly(pIn);
                if (outputGobbler != null && !outputGobbler.isInterrupted()) {
                    outputGobbler.interrupt();
                }
            }
            if (pErr != null) {
                this.closeQuietly(pErr);
                if (errorGobbler != null && !errorGobbler.isInterrupted()) {
                    errorGobbler.interrupt();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    private void closeQuietly(InputStream c) {
        try {
            if (c != null)
                c.close();
        } catch (IOException e) {
        }
    }
}
