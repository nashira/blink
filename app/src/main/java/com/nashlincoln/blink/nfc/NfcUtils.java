package com.nashlincoln.blink.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.nashlincoln.blink.BuildConfig;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.network.BlinkApi;
import com.nashlincoln.blink.ui.BlinkActivity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by nash on 11/2/14.
 */
public class NfcUtils {
    private static final String TAG = "NfcUtils";

    public static void readTag(Context context, Intent intent) {

        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];

            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
                NdefRecord record = messages[i].getRecords()[0];
                String text = new String(record.getPayload());
                List<NfcCommand> commands = BlinkApi.getGson().fromJson(text, new TypeToken<List<NfcCommand>>(){}.getType());
                Syncro.getInstance().applyNfcCommands(commands);
            }
        }
    }

    public static void stageWrite(Activity activity, String data) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);

        Intent nfcIntent = new Intent(activity, BlinkActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        nfcIntent.putExtra(BlinkApp.EXTRA_NFC_WRITE, true);
        nfcIntent.putExtra(Intent.EXTRA_TEXT, data);
        PendingIntent pi = PendingIntent.getActivity(activity, 0, nfcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        nfcAdapter.enableForegroundDispatch(activity, pi, new IntentFilter[]{tagDetected}, null);
        Toast.makeText(activity, "Touch NFC Tag to write", Toast.LENGTH_SHORT).show();
    }

    public static void writeTag(final Activity activity, final Intent intent) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String data = intent.getStringExtra(Intent.EXTRA_TEXT);

                NdefRecord payload = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                        ("application/" + BuildConfig.APPLICATION_ID).getBytes(Charset.forName("US-ASCII")),
                        null, data.getBytes());

                NdefMessage message = new NdefMessage(new NdefRecord[]{payload});

                try {
                    // If the tag is already formatted, just write the message to it
                    Ndef ndef = Ndef.get(tag);
                    if (ndef != null) {
                        ndef.connect();

                        // Make sure the tag is writable
                        if (!ndef.isWritable()) {
                            return "Error: tag is not writable";
                        }

                        // Check if there's enough space on the tag for the message
                        int size = message.toByteArray().length;
                        if (ndef.getMaxSize() < size) {
                            return String.format("message size (%d) exceeds tag size (%d)", size, ndef.getMaxSize());
                        }

                        try {
                            // Write the data to the tag
                            ndef.writeNdefMessage(message);
                            return "Tag written";
                        } catch (TagLostException tle) {
                            return "Error: tag lost";
                        } catch (IOException | FormatException ioe) {
                            return "Error: formatting error";
                        }
                        // If the tag is not formatted, format it with the message
                    } else {
                        NdefFormatable format = NdefFormatable.get(tag);
                        if (format != null) {
                            try {
                                format.connect();
                                format.format(message);

                                return "Tag written";
                            } catch (TagLostException tle) {
                                return "Error: tag lost";
                            } catch (IOException | FormatException ioe) {
                                return "Error: formatting error";
                            }
                        } else {
                            return  "Error: not able to format";
                        }
                    }
                } catch (Exception e) {
                    return  "Error: unknown";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null) {
                    Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
