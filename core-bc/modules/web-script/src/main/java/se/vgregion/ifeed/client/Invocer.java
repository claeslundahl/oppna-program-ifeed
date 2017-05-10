package se.vgregion.ifeed.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.*;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

public class Invocer {


  public static void fetchFeedByJsonpCall(String url, Callback<List<Entry>> success, Callback<Throwable> error) {
    fetchFeedByJsonpCall(url, new AsyncCallback<List<Entry>>() {
      @Override
      public void onFailure(Throwable caught) {
        error.event(caught);
      }

      @Override
      public void onSuccess(List<Entry> result) {
        success.event(result);
      }
    });
  }

  public static void fetchFeedByJsonpCall(String url, Callback<List<Entry>> success) {
    fetchFeedByJsonpCall(url, new AsyncCallback<List<Entry>>() {
      @Override
      public void onFailure(Throwable caught) {
        Window.alert(caught.toString());
      }

      @Override
      public void onSuccess(List<Entry> result) {
        success.event(result);
      }
    });
  }

  private static void fetchFeedByJsonpCall(String url, AsyncCallback<List<Entry>> callback) {
    if (url.length() > 300) {
      int indexOfData = url.indexOf('?');
      String data = url.substring(indexOfData + 1);
      url = url.substring(0, indexOfData);
      RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
      builder.setHeader("Content-type", "application/x-www-form-urlencoded");
      try {
        builder.sendRequest(data, new RequestCallback() {
          @Override
          public void onResponseReceived(Request request, Response response) {
            callback.onSuccess(toEntries(response.getText()));
          }

          @Override
          public void onError(Request request, Throwable throwable) {
            Util.log(throwable);
          }
        });
      } catch (Exception e) {
        Util.log(e);
      }
    } else {
      JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
      jsonp.requestObject(url, new AsyncCallback<JavaScriptObject>() {
        @Override
        public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }

        @Override
        public void onSuccess(JavaScriptObject result) {
          /*JsArray array = (JsArray) result;
          List<Entry> entries = new ArrayList<>();
          for (int i = 0, j = array.length(); i < j; i++) {
            Entry item = array.get(i).cast();
            item.get("title");
            entries.add(item);
          }
          GWT.log("Success: " + result.toString());
          callback.onSuccess(entries);*/
          callback.onSuccess(toEntries((JsArray) result));
        }
      });
    }
  }

  private static List<Entry> toEntries(String text) {
    JavaScriptObject jso = JsonUtils.safeEval(text);
    return toEntries((JsArray) jso);
  }

  private static List<Entry> toEntries(JsArray array) {
    List<Entry> entries = new ArrayList<>();
    for (int i = 0, j = array.length(); i < j; i++) {
      Entry item = array.get(i).cast();
      // item.get("title"); // Why where this here?
      entries.add(item);
    }
    return entries;
  }

    /*public static void fetchMeta(Callback callback) {
        String url = "http://vgas1499.vgregion.se:9090/solr/ifeed/schema";
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);

        rb.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                GWT.log(request.toString());
                GWT.log(response.toString());
            }

            @Override
            public void onError(Request request, Throwable exception) {
                GWT.log(request.toString());
                GWT.log(exception.toString());
            }
        });

        try {
            rb.send();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }*/


  public interface Callback<T> {
    void event(T t);
  }


}