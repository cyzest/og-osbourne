package com.cyzest.ogosbourne.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConvertRequestHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

    }

}
