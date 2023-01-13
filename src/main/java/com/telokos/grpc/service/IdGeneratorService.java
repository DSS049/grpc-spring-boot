package com.telokos.grpc.service;

import com.google.protobuf.Empty;
import com.telokos.grpc.IDGeneratorResponse;
import com.telokos.grpc.IdGeneratorServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class IdGeneratorService extends IdGeneratorServiceGrpc.IdGeneratorServiceImplBase {
  @Override
  public void generate(Empty request, StreamObserver<IDGeneratorResponse> responseObserver) {
    log.info("Received request to generate bookingId");
    IDGeneratorResponse response =
        IDGeneratorResponse.newBuilder().setId(UUID.randomUUID().toString()).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
