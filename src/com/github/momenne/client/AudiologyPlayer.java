package com.github.momenne.client;

import com.github.momenne.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.miller.gwt.client.sound.Callback;
import org.miller.gwt.client.sound.ID3;
import org.miller.gwt.client.sound.SMSound;
import org.miller.gwt.client.sound.SoundManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AudiologyPlayer implements EntryPoint {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);


    private SoundManager sm = SoundManager.getInstance();
    private String SOUND_ID = "soundID";

    private int playListIndex = 0;

    private List<String> soundIDs = new ArrayList<String>();

        /**
         * This is the entry point method.
         */
        public void onModuleLoad() {
            final Button sendButton = new Button("Submit");
            final TextBox nameField = new TextBox();
            nameField.setText("password");
            final Label errorLabel = new Label();

            // We can add style names to widgets
            sendButton.addStyleName("sendButton");

            // Add the nameField and sendButton to the RootPanel
            // Use RootPanel.get() to get the entire body element
            RootPanel.get("nameFieldContainer").add(nameField);
            RootPanel.get("sendButtonContainer").add(sendButton);
            RootPanel.get("errorLabelContainer").add(errorLabel);

            // Focus the cursor on the name field when the app loads
            nameField.setFocus(true);
            nameField.selectAll();

            // Create the popup dialog box
            final DialogBox dialogBox = new DialogBox();
            dialogBox.setText("Verified");
            dialogBox.setAnimationEnabled(true);
            final Button closeButton = new Button("Proceed to test suite");
            // We can set the id of a widget by accessing its Element
            closeButton.getElement().setId("closeButton");
            VerticalPanel dialogVPanel = new VerticalPanel();
            dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
            dialogVPanel.add(closeButton);
            dialogBox.setWidget(dialogVPanel);

            // Add a handler to close the DialogBox
            closeButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    dialogBox.hide();
                    sendButton.setEnabled(true);
                    sendButton.setFocus(true);
                    Window.Location.assign("/playlists");
                }
            });

            // Create a handler for the sendButton and nameField
            class MyHandler implements ClickHandler, KeyUpHandler {
                /**
                 * Fired when the user clicks on the sendButton.
                 */
                public void onClick(ClickEvent event) {
                    sendNameToServer();
                }

                /**
                 * Fired when the user types in the nameField.
                 */
                public void onKeyUp(KeyUpEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                        sendNameToServer();
                    }
                }

                /**
                 * Send the name from the nameField to the server and wait for a response.
                 */
                private void sendNameToServer() {
                    // First, we validate the input.
                    errorLabel.setText("");
                    String textToServer = nameField.getText();
                    if (!FieldVerifier.isValidName(textToServer)) {
                        errorLabel.setText("Please enter at least four characters");
                        return;
                    }

                    // Then, we send the input to the server.
                    sendButton.setEnabled(false);
                    greetingService.greetServer(textToServer,
                            new AsyncCallback<String>() {
                                public void onFailure(Throwable caught) {
                                    // Show the RPC error message to the user
                                    dialogBox
                                            .setText("Remote Procedure Call - Failure");
                                    dialogBox.center();
                                    closeButton.setFocus(true);
                                }

                                public void onSuccess(String result) {
                                    dialogBox.setText("Remote Procedure Call");
                                    dialogBox.center();
                                    closeButton.setFocus(true);
                                }
                            });
                }
            }

            // Add a handler to send the name to the server
            MyHandler handler = new MyHandler();
            sendButton.addClickHandler(handler);
            nameField.addKeyUpHandler(handler);
        }

}
