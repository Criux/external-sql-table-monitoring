package com.kmarinos.externalsqltablemonitoring.exceptionhandling;

import com.kmarinos.externalsqltablemonitoring.exceptionhandling.exceptions.CannotParseInputException;
import com.kmarinos.externalsqltablemonitoring.exceptionhandling.exceptions.EntityNotFoundException;
import com.kmarinos.externalsqltablemonitoring.exceptionhandling.exceptions.InputValidationException;
import com.kmarinos.externalsqltablemonitoring.exceptionhandling.exceptions.MissingEntityReferenceException;
import com.kmarinos.externalsqltablemonitoring.exceptionhandling.exceptions.OperationNotAllowedException;
import com.kmarinos.externalsqltablemonitoring.exceptionhandling.model.ApiError;
import com.kmarinos.externalsqltablemonitoring.exceptionhandling.model.ApiSubError;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handle {@link MissingServletRequestParameterException}. Triggered when a 'required' request parameter is missing.
   *
   * @param ex MissingServletRequestParameterException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  protected ResponseEntity<Object>handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request){
    String error = ex.getParameterName()+" parameter is missing";
    return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST,error,ex));
  }

  /**
   * Handle {@link MissingPathVariableException}. Triggered when a 'required' path parameter is missing.
   *
   * @param ex MissingPathVariableException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  public final ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,HttpHeaders headers, HttpStatus status,WebRequest request){
    String error = ex.getVariableName() + " path parameter is missing";
    return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST,error,ex));
  }

  /**
   * Handle {@link HttpMediaTypeNotSupportedException}. This one triggers when JSON is invalid as well.
   *
   * @param ex HttpMediaTypeNotSupportedException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  protected  ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,HttpHeaders headers, HttpStatus status, WebRequest request){
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t->builder.append(t).append(", "));
    return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE,builder.substring(0,builder.length()-2),ex));
  }

  /**
   * Handle {@link MethodArgumentNotValidException}. Triggered when an object fails @Valid validation.
   *
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return
   */
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request
  ){
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
    apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
    return buildResponseEntity(apiError);
  }

  /**
   * Handles {@link ConstraintViolationException}. Thrown when @Validated fails.
   *
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex,
      HttpServletRequest request){
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Validation error - "+ex.getConstraintName());
    return buildResponseEntity(apiError,request);
  }

  /**
   * Handles {@link EntityNotFoundException}. Created to encapsulate errors with more detail than {@link jakarta.persistence.EntityNotFoundException}
   *
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex,HttpServletRequest request){
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setError("Entity not found");
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError,request);
  }
  @ExceptionHandler(InputValidationException.class)
  protected ResponseEntity<Object> handleEntityNotFound(InputValidationException ex,HttpServletRequest request){
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setError("Input Validation");
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError,request);
  }

  /**
   * Handles {@link CannotParseInputException}
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(CannotParseInputException.class)
  protected ResponseEntity<Object> handleCannotParseInput(CannotParseInputException ex){
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }
  @ExceptionHandler(MissingEntityReferenceException.class)
  protected ResponseEntity<Object> handleMissingEntityReference(MissingEntityReferenceException ex){
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * Handles {@link OperationNotAllowedException}. Created to disallow calls that would result in a conflicting state.
   *
   * @param ex
   * @return
   */
  @Nullable
  @ExceptionHandler(OperationNotAllowedException.class)
  protected ResponseEntity<Object> handleOperationNotAllowed(OperationNotAllowedException ex){
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }
  @Nullable
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,HttpHeaders headers, HttpStatusCode status,WebRequest request){
    ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED);
    apiError.setError("Method Not Allowed");
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError,((ServletWebRequest)request).getRequest());
  }

  /**
   * Handles {@link HttpMessageNotReadableException}. Happens when request JSON is malformed.
   *
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return
   */
  @Nullable
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,HttpHeaders headers, HttpStatusCode status,WebRequest request){
    return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST,"Malformed JSON request",ex),((ServletWebRequest)request).getRequest());
  }
  @Nullable
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      HttpMessageNotWritableException ex,HttpHeaders headers, HttpStatusCode status,WebRequest request){
    return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"Error writing JSON output",ex),((ServletWebRequest)request).getRequest());
  }
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex
  ){
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(String.format("Could not find the %s method for URL %s",ex.getHttpMethod(),ex.getRequestURL()));
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * Handle {@link DataIntegrityViolationException}, inspects the cause for different DB causes.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  protected ResponseEntity<Object> handleDatIntegrityViolation(DataIntegrityViolationException ex){
    if(ex.getCause() instanceof ConstraintViolationException){
      return buildResponseEntity(new ApiError(HttpStatus.CONFLICT,"Database error",ex.getCause()));
    }
    return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,ex));
  }
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<?>handleGenericException(Exception ex){
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(ex.getMessage());
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ex.printStackTrace(pw);
    apiError.setDebugMessage(sw.toString());
    log.error("Exception",ex);
    return buildResponseEntity(apiError);
  }
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<Object>handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex){
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",ex.getName(),ex.getValue(),ex.getRequiredType().getSimpleName()));
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }






  private ResponseEntity<Object> buildResponseEntity(ApiError apiError,HttpServletRequest request){
    apiError.setPath(request.getRequestURI());
    return buildResponseEntity(apiError);
  }
  private ResponseEntity<Object> buildResponseEntity(ApiError apiError){
    return new ResponseEntity<>(apiError,apiError.getHttpStatus());
  }
  static String extractPostRequestBody(HttpServletRequest request){
    if("POST".equalsIgnoreCase(request.getMethod())){
      Scanner s = null;
      try{
        s = new Scanner(request.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A");
      }catch (IOException e){
        e.printStackTrace();
      }
      return s.hasNext()?s.next():"";
    }
    return "";
  }
}
