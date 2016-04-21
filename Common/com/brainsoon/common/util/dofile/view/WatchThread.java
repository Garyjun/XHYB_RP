package com.brainsoon.common.util.dofile.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * BSRCM应用com.brainsoon.bsrcm.common.WatchThread.java
 * 创建时间：2011-11-11	
 * 创建者： liusy
 * WatchThread
 * TODO
 *
 */
public class WatchThread extends Thread {
	private static final Log logger = LogFactory.getLog(WatchThread.class);
    InputStream input;
    boolean over;

    public WatchThread(InputStream input) {
        this.input = input;
        over = false;
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    input));
            while (true) {
                if (over) {
                    break;
                }
                
                String info =br.readLine();
                while (info != null){
                	logger.debug(info);
                	info =br.readLine();
                }
            }
        } catch (Exception e) {
        	logger.error(e);
        }
    }

    public void setOver(boolean over) {
        this.over = over;
    }
}