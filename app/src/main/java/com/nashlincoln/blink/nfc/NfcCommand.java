package com.nashlincoln.blink.nfc;

import java.util.List;

/**
 * Created by nash on 11/2/14.
 */
public class NfcCommand {
    public Long d;
    public Long g;
    public List<Update> u;

    public static class Update {
        public Long i;
        public String v;
    }
}
