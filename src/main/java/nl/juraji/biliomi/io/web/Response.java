package nl.juraji.biliomi.io.web;

import org.eclipse.jetty.client.api.Request;

import javax.enterprise.inject.Vetoed;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@Vetoed
public class Response<T> {
  private Request request;
  private int status;
  private String rawData;
  private T data;

  public Request getRequest() {
    return request;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getRawData() {
    return rawData;
  }

  public void setRawData(String rawData) {
    this.rawData = rawData;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public boolean isOK() {
    return status == 200 || status == 201;
  }

  @Override
  public String toString() {
    return "Response{" +
        "request=" + request +
        ", status=" + status +
        ", rawData='" + rawData + '\'' +
        '}';
  }
}
