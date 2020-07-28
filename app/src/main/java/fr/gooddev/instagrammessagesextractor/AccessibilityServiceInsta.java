package fr.gooddev.instagrammessagesextractor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class AccessibilityServiceInsta extends AccessibilityService {

    String date, message, type, person, data, lastdata, lastmessage;

    public void processChild(AccessibilityNodeInfo source) {
        person = "";
        lastdata = "";

        if(source == null) {
            return;
        }

        Integer currentChild = source.getChildCount();
        if(currentChild>0) {
            for(Integer i=0; i<currentChild; i++) {
                processChild(source.getChild(i));
            }
        }

        else {
            if (isAMessagePage(source)) {
                if (source.getClassName().equals("android.widget.TextView") && source.getText() != null && !source.getText().toString().isEmpty() && source.getParent().getParent().getClassName().equals("android.support.v7.widget.RecyclerView")) {
                    if (!isDate(source.getText().toString())) {
                        if (source.toString().contains("importantForAccessibility: true")) {
                            if (source.isFocusable()) {
                                //Define if the message is incoming or outgoing
                                DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
                                int width = metrics.widthPixels;
                                int widthtoleft = 0;
                                String widthdata = source.toString().substring(source.toString().indexOf("boundsInScreen: Rect(") + 21, source.toString().indexOf("boundsInScreen: Rect(") + 24);
                                if (widthdata != null && isParsable(widthdata)) {
                                    widthtoleft = Integer.parseInt(widthdata);
                                }
                                if (widthtoleft > (width / 5.5)) {
                                    message = source.getText().toString().replace("\"", "\\\"");
                                    type = "outgoing";
                                } else if (widthtoleft < (width / 5.5)) {
                                    message = source.getText().toString().replace("\"", "\\\"");
                                    type = "incoming";
                                }
                            }
                        }
                    }
                    else {
                        date = source.getText().toString();
                        person = getInterlocutor(source);
                        if (person != null && !person.equals("")) {
                            if (!lastmessage.equals(message)) {
                                data += "[\"" + person + "\", \"" + message + "\", \"" + type + "\", \"" + date + "\"],";
                            }
                            else {
                                data += "[\"" + person + "\", \"Média ou Story\", \"" + type + "\", \"" + date + "\"],";
                            }
                        }
                        lastmessage = message;
                    }
                }
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        SharedPreferences sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);

        if (source == null) {
            return;
        }

        data = "";
        lastmessage = "";
        message = "";

        processChild(source);

        if ((data.length() - 1) > 0) {
            data = data.substring(0,(data.length() - 1));
        }

        lastdata = sharedPreferences.getString("LAST_INSTAGRAM_MESSAGE_OUTPUT", "LAST_INSTAGRAM_MESSAGE_OUTPUT");

        if (!lastdata.equals(data) && data != null && data != "" && !lastdata.equals("LAST_INSTAGRAM_MESSAGE_OUTPUT")) {
            Toast.makeText(this, data, Toast.LENGTH_LONG).show();
            Log.d("data", data);
            sharedPreferences.edit().putString("LAST_INSTAGRAM_MESSAGE_OUTPUT", data).apply();
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        Toast.makeText(this, "Service connected", Toast.LENGTH_LONG).show();
        // Set the type of events that this service wants to listen to. Others won't be passed to this service.
        // We are only considering windows state changed event.
        info.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        // If you only want this service to work with specific applications, set their package names here. Otherwise, when the service is activated, it will listen to events from all applications.
        info.packageNames = new String[] {"com.instagram.android"};
        // Set the type of feedback your service will provide. We are setting it to GENERIC.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        // Default services are invoked only if no package-specific ones are present for the type of AccessibilityEvent generated.
        // This is a general-purpose service, so we will set some flags
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS; info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY; info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        // We are keeping the timeout to 0 as we don’t need any delay or to pause our accessibility events
        info.notificationTimeout = 0;
        this.setServiceInfo(info);
    }

    public static boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public boolean isDate(String s) {
        String pattern = "([0-9]{1,2}):([0-9]{2}) [AP]M";
        return s.matches(pattern);
    }

    public boolean isAMessagePage(AccessibilityNodeInfo source) {
        if (source.getClassName() != null && source.getParent() != null) {
            if (source.getParent().getParent() != null) {
                if (source.getParent().getParent().getParent() != null) {
                    if (source.getParent().getParent().getParent().getParent() != null) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getInterlocutor(AccessibilityNodeInfo source) {
        if (source.getParent().getParent().getParent().getParent().getChild(0) != null) {
            if (source.getParent().getParent().getParent().getParent().getChild(0).getChild(0) != null) {
                if (source.getParent().getParent().getParent().getParent().getChild(0).getChild(0).getChild(1) != null) {
                    if (source.getParent().getParent().getParent().getParent().getChild(0).getChild(0).getChild(1).getChild(0) != null) {
                        return String.valueOf(source.getParent().getParent().getParent().getParent().getChild(0).getChild(0).getChild(1).getChild(0).getText());
                    }
                    else {
                        return "";
                    }
                }
                else {
                    return "";
                }
            }
            else {
                return "";
            }
        }
        else {
            return "";
        }
    }

}