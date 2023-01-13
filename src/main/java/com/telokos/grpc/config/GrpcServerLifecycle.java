package com.telokos.grpc.config;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class GrpcServerLifecycle implements SmartLifecycle {
  private Server server;
  private final BindableService service;
  private final GrpcProperties grpcProperties;

  public void start() {
    try {
      this.createAndStartGrpcServer();
    } catch (IOException ex) {
      log.error("Failed to start the grpc server");
      throw new IllegalStateException(ex);
    }
  }

  public void stop() {
    this.stopAndReleaseGrpcServer();
  }

  public boolean isRunning() {
    return this.server != null && !this.server.isShutdown();
  }

  private void createAndStartGrpcServer() throws IOException {
    if (this.server == null) {
      Server localServer = ServerBuilder.forPort(grpcProperties.getPort()).addService(service).build();
      this.server = localServer;
      localServer.start();
      log.info("gRPC Server started, listening on port: {}", grpcProperties.getPort());
      Thread awaitThread =
          new Thread(
              () -> {
                try {
                  localServer.awaitTermination();
                } catch (InterruptedException var2) {
                  Thread.currentThread().interrupt();
                }
              });
      awaitThread.setDaemon(false);
      awaitThread.start();
    }
  }

  private void stopAndReleaseGrpcServer() {
    Server localServer = this.server;
    if (localServer != null) {
      long millis = Duration.of(10L, ChronoUnit.SECONDS).toMillis();
      log.debug("Initiating gRPC server shutdown");
      localServer.shutdown();
      try {
        if (millis > 0L) {
          localServer.awaitTermination(millis, TimeUnit.MILLISECONDS);
        } else if (millis != 0L) {
          localServer.awaitTermination();
        }
      } catch (InterruptedException var8) {
        Thread.currentThread().interrupt();
      } finally {
        localServer.shutdownNow();
        this.server = null;
      }
      log.info("Completed gRPC server shutdown");
    }
  }
}
