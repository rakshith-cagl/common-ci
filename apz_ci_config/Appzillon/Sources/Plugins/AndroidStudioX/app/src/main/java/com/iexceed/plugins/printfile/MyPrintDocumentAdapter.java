package com.iexceed.plugins.printfile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

	private String filePath;

	public MyPrintDocumentAdapter(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void onLayout(PrintAttributes oldAttributes,	PrintAttributes newAttributes,CancellationSignal cancellationSignal,LayoutResultCallback callback, Bundle extras) {
		
		if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

//        int pages = computePageCount(newAttributes);

        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("file_print").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

        callback.onLayoutFinished(pdi, true);
		
	}

	@Override
	public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,CancellationSignal cancellationSignal, WriteResultCallback callback) {
		
		InputStream input = null;
        OutputStream output = null;
        FileOutputStream b = null;

        try {
//        	String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/personal/tpf.doc";
//            input = new FileInputStream(filePath);
            input = new FileInputStream(AppzillonUtils.validatePath(filePath,null));
//            output = new FileOutputStream(destination.getFileDescriptor());
            b = new FileOutputStream(destination.getFileDescriptor());
//            byte[] buf = new byte[1024];
            byte[] buf = new byte[4096];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
//                 output.write(buf, 0, bytesRead);
            	b.write(buf, 0, bytesRead);
            }

            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (FileNotFoundException ee){
            //Catch exception
        } catch (Exception e) {
            //Catch exception
        } finally {
            try {
                input.close();
//                output.close();
                b.close();
            } catch (IOException e) {
                ApzLogger.e("MyPrintDocumentAdapter" ,e.toString());
            }
        }
	}
	
	


}
