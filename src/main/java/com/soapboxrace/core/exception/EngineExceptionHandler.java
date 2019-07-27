package com.soapboxrace.core.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EngineExceptionHandler implements ExceptionMapper<EngineException> {
    @Override
    public Response toResponse(EngineException exception) {
        EngineExceptionTrans engineExceptionTrans = new EngineExceptionTrans();

        /* <EngineExceptionTrans xmlns=\"http://schemas.datacontract.org/2004/07/Victory.Service\">" +
                            "<ErrorCode>-1613</ErrorCode>" +
                            "<InnerException>" +
                            "<ErrorCode>-1613</ErrorCode>" +
                            "</InnerException>" +
                            "</EngineExceptionTrans> */


        engineExceptionTrans.setErrorCode(exception.getCode().getErrorCode());
        String stackTrace = ExceptionUtils.getStackTrace(exception);
        engineExceptionTrans.setStackTrace(stackTrace);
        engineExceptionTrans.setInnerException(new EngineInnerExceptionTrans());
        engineExceptionTrans.getInnerException().setErrorCode(engineExceptionTrans.getErrorCode());
        engineExceptionTrans.getInnerException().setStackTrace(stackTrace);

        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .type(MediaType.APPLICATION_XML_TYPE)
                .entity(engineExceptionTrans).build();
    }
}
