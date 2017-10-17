package nl.juraji.biliomi.io.web;

import com.google.common.net.MediaType;
import org.eclipse.jetty.http.HttpFields;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 *
 * Note: If the given model class is a Java type the response data will not be set.
 * The response raw data will always contain a string of the response contents.
 */
public interface WebClient {

  /**
   * This header name should be present in the request headers if the caller
   * wishes this request to not be cached.
   * Implementors should always obey this header when present
   */
  String NO_CACHE_HEADER = "WEBCLIENT_NO_CACHE";

  /**
   * Perform a GET request
   *
   * @param uri     The uri to the webresource
   * @param headers A HttpFields object containing headers to use
   * @param model   The model class expected to be returned by the webresource
   * @param <T>     Generictype model class, to assign a type to the response
   * @return A Response object containing status information and any returned data
   * @throws Exception When the request fails to execute due to insufficient parameters or an internal error
   */
  <T> Response<T> get(String uri, HttpFields headers, Class<T> model) throws Exception;

  /**
   * Perform a POST request
   *
   * @param <T>           Generictype model class, to assign a type to the response
   * @param uri           The uri to the webresource
   * @param headers       A HttpFields object containing headers to use
   * @param body          A String containing the body to post the the webresource
   * @param bodyMediaType The MediaType of the body, this is required for the webresource to understand the body
   * @param model         The model class expected to be returned by the webresource
   * @return A Response object containing status information and any returned data
   * @throws Exception When the request fails to execute due to insufficient parameters or an internal error
   */
  <T> Response<T> post(String uri, HttpFields headers, String body, MediaType bodyMediaType, Class<T> model) throws Exception;

  /**
   * Perform a PUT request
   *
   * @param <T>           Generictype model class, to assign a type to the response
   * @param uri           The uri to the webresource
   * @param headers       A HttpFields object containing headers to use
   * @param body          A String containing the body to post the the webresource
   * @param bodyMediaType The MediaType of the body, this is required for the webresource to understand the body
   * @param model         The model class expected to be returned by the webresource
   * @return A Response object containing status information and any returned data
   * @throws Exception When the request fails to execute due to insufficient parameters or an internal error
   */
  <T> Response<T> put(String uri, HttpFields headers, String body, MediaType bodyMediaType, Class<T> model) throws Exception;

  default <T> Response<T> get(Url url, HttpFields headers, Class<T> model) throws Exception {
    return get(url.toString(), headers, model);
  }

  default <T> Response<T> post(Url url, HttpFields headers, String body, MediaType bodyMediaType, Class<T> model) throws Exception {
    return post(url.toString(), headers, body, bodyMediaType, model);
  }

  default <T> Response<T> put(Url url, HttpFields headers, String body, MediaType bodyMediaType, Class<T> model) throws Exception {
    return put(url.toString(), headers, body, bodyMediaType, model);
  }
}
