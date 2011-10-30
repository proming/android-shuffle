/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dodgybits.shuffle.gwt;

import java.util.Date;
import java.util.List;

import org.dodgybits.shuffle.client.ShuffleRequestFactory;
import org.dodgybits.shuffle.client.ShuffleRequestFactory.HelloWorldRequest;
import org.dodgybits.shuffle.client.ShuffleRequestFactory.MessageRequest;
import org.dodgybits.shuffle.gwt.formatter.ActionDateFormatter;
import org.dodgybits.shuffle.shared.MessageProxy;
import org.dodgybits.shuffle.shared.TaskProxy;
import org.dodgybits.shuffle.shared.TaskService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class ShuffleWidget extends Composite {
  private static final int STATUS_DELAY = 4000;
  private static final String STATUS_ERROR = "status error";
  private static final String STATUS_NONE = "status none";
  private static final String STATUS_SUCCESS = "status success";

  interface ShuffleUiBinder extends UiBinder<Widget, ShuffleWidget> {
  }

  private static ShuffleUiBinder uiBinder = GWT.create(ShuffleUiBinder.class);

  private ActionDateFormatter mFormatter;
  
  @UiField
  TextAreaElement messageArea;

  @UiField
  InputElement recipientArea;

  @UiField
  DivElement status;

  @UiField
  Button sayHelloButton;

  @UiField
  Button sendMessageButton;
  
  @UiField
  Button fetchTasksButton;

  @UiField
  FlexTable table;
  
  /**
   * Timer to clear the UI.
   */
  Timer timer = new Timer() {
    @Override
    public void run() {
      status.setInnerText("");
      status.setClassName(STATUS_NONE);
      recipientArea.setValue("");
      messageArea.setValue("");
    }
  };

  private void setStatus(String message, boolean error) {
    status.setInnerText(message);
    if (error) {
      status.setClassName(STATUS_ERROR);
    } else {
      if (message.length() == 0) {
        status.setClassName(STATUS_NONE);
      } else {
        status.setClassName(STATUS_SUCCESS);
      }
    }

    timer.schedule(STATUS_DELAY);
  }

  public ShuffleWidget() {
    initWidget(uiBinder.createAndBindUi(this));
    sayHelloButton.getElement().setClassName("send centerbtn");
    sendMessageButton.getElement().setClassName("send");

    mFormatter = new ActionDateFormatter();
    
    final EventBus eventBus = new SimpleEventBus();
    final ShuffleRequestFactory requestFactory = GWT.create(ShuffleRequestFactory.class);
    requestFactory.initialize(eventBus);

    sendMessageButton.addClickHandler(new ClickHandler() {
    public void onClick(ClickEvent event) {
        String recipient = recipientArea.getValue();
        String message = messageArea.getValue();
        setStatus("Connecting...", false);
        sendMessageButton.setEnabled(false);

        // Send a message using RequestFactory
        MessageRequest request = requestFactory.messageRequest();
        MessageProxy messageProxy = request.create(MessageProxy.class);
        messageProxy.setRecipient(recipient);
        messageProxy.setMessage(message);
        Request<String> sendRequest = request.send().using(messageProxy);
        sendRequest.fire(new Receiver<String>() {
          @Override
          public void onFailure(ServerFailure error) {
            sendMessageButton.setEnabled(true);
            setStatus(error.getMessage(), true);
          }

          @Override
          public void onSuccess(String response) {
            sendMessageButton.setEnabled(true);
            setStatus(response, response.startsWith("Failure:"));
          }
        });
      }
    });

    fetchTasksButton.addClickHandler(new ClickHandler() {
    public void onClick(ClickEvent event) {
        setStatus("Connecting...", false);
        fetchTasksButton.setEnabled(false);

        // Send a message using RequestFactory
        TaskService service = requestFactory.taskService();
        Request<List<TaskProxy>> taskListRequest = service.listAll();
        taskListRequest.fire(new Receiver<List<TaskProxy>>() {
            @Override
            public void onFailure(ServerFailure error) {
                fetchTasksButton.setEnabled(true);
              setStatus(error.getMessage(), true);
            }

            @Override
            public void onSuccess(List<TaskProxy> tasks) {
                fetchTasksButton.setEnabled(true);
              setStatus("Success - got " + tasks.size(), false);
              displayActions(tasks);
            }
          });        
      }
    
    });

    sayHelloButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        sayHelloButton.setEnabled(false);
        HelloWorldRequest helloWorldRequest = requestFactory.helloWorldRequest();
        helloWorldRequest.getMessage().fire(new Receiver<String>() {
          @Override
          public void onFailure(ServerFailure error) {
            sayHelloButton.setEnabled(true);
            setStatus(error.getMessage(), true);
          }

          @Override
          public void onSuccess(String response) {
            sayHelloButton.setEnabled(true);
            setStatus(response, response.startsWith("Failure:"));
          }
        });
      }
    });
  }
  
  private void displayActions(List<TaskProxy> tasks) {
      int numActions = tasks.size();
      for (int i = 0; i < numActions; i++) {
          TaskProxy taskValue = tasks.get(i);
          displayAction(taskValue, i);
      }
  }
 
  private void displayAction(TaskProxy taskValue, int row) {
      String description = "<div class='actionTitle'>"
              + escapeHtml(taskValue.getDescription())
              + "<span class='actionDetails'> - "
              + escapeHtml(taskValue.getDetails()) + "</span></div>";
      table.setHTML(row, 0, description);

      table.setText(row, 1, mFormatter.getShortDate(taskValue.getModifiedDate()));
      table.getCellFormatter().setStyleName(
              row,
              1,
              isInPast(taskValue.getModifiedDate()) ? "actionDueInPass"
                      : "actionDueInFuture");
  }

  private static String escapeHtml(String maybeHtml) {
      final Element div = DOM.createDiv();
      DOM.setInnerText(div, maybeHtml);
      return DOM.getInnerHTML(div);
  }

  private static boolean isInPast(Date date) {
      return date != null && date.getTime() < System.currentTimeMillis();
  }
  
}
