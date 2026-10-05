package com.iexceed.plugins.fileoperation;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iexceed.appzillonapp.R;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by rema.krishnan on 19/11/18.
 */

public class CustomArrayAdapter extends ArrayAdapter<File> {

    private final Activity context;
   File[] files;
      ArrayList<File> items;


    public CustomArrayAdapter(Activity context, int resource, ArrayList<File> itemname) {
        super(context, R.layout.filebrowser_list_row,itemname);

        // TODO Auto-generated constructor stub

        this.context=context;
        items=itemname;
            }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.filebrowser_list_row, null,true);
        files= (File[]) items.toArray(new File[items.size()]);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.rowtext);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
      //  TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(files[position].getName());
        if(files[position].isDirectory()) {
            imageView.setImageResource(R.drawable.folder);
        }
        else{
            Uri selectedUri = Uri.fromFile(files[position]);
            String fileExt= MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt);
            if(mimeType!=null) {
                if (fileExt.equalsIgnoreCase("pdf"))
                    imageView.setImageResource(R.drawable.pdf);
                else if (fileExt.equalsIgnoreCase("txt") || fileExt.equalsIgnoreCase("text"))
                    imageView.setImageResource(R.drawable.text);
                else if (fileExt.equalsIgnoreCase("doc") || fileExt.equalsIgnoreCase("docx"))
                    imageView.setImageResource(R.drawable.doc);
                else if (fileExt.equalsIgnoreCase("ppt") || fileExt.equalsIgnoreCase("pptx") || fileExt.equalsIgnoreCase("pptm"))
                    imageView.setImageResource(R.drawable.ppt);
                else if (fileExt.equalsIgnoreCase("war") || fileExt.equalsIgnoreCase("zip") || fileExt.equalsIgnoreCase("jar"))
                    imageView.setImageResource(R.drawable.archive);
                else if (fileExt.equalsIgnoreCase("html") || fileExt.equalsIgnoreCase("htm"))
                    imageView.setImageResource(R.drawable.html);
                else if (fileExt.equalsIgnoreCase("apk"))
                    imageView.setImageResource(R.drawable.apk);
                else if (fileExt.equalsIgnoreCase("xls") || fileExt.equalsIgnoreCase("xlsx"))
                    imageView.setImageResource(R.drawable.xlsx);
                else if (fileExt.equalsIgnoreCase("png") || fileExt.equalsIgnoreCase("jpeg") || fileExt.equalsIgnoreCase("jpg"))
                    imageView.setImageResource(R.drawable.image);
                else if (fileExt.equalsIgnoreCase("mp3"))
                    imageView.setImageResource(R.drawable.audio);
                else if (fileExt.equalsIgnoreCase("mp4"))
                    imageView.setImageResource(R.drawable.video);
                else if (fileExt.equalsIgnoreCase("xml"))
                    imageView.setImageResource(R.drawable.xml);
                else
                    imageView.setImageResource(R.drawable.simplefile);
            }else
                imageView.setImageResource(R.drawable.unknownfile);
        }
       // extratxt.setText("Description "+itemname[position]);
        return rowView;

    };

}

