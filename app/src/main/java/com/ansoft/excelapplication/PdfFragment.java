package com.ansoft.excelapplication;


import android.annotation.SuppressLint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class PdfFragment extends Fragment {

    PDFView pdfView;
    Slide currentSlide;
    File currentPDF;
    static enum Slide {
        VUR, SUMMARY, ROB, VEF
    }

    public PdfFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public PdfFragment(Slide currentSlide){
        this.currentSlide=currentSlide;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_pdf, container, false);
        pdfView=(PDFView)view.findViewById(R.id.pdfView);
        getCurrentSlide();
        //pdfView.fromFile(currentPDF);
        Log.e("PDF", currentPDF.getAbsolutePath());
        pdfView.fromFile(currentPDF)
                .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .enableDoubletap(true)
                .defaultPage(0)
                .onDraw(null) // allows to draw something on a provided canvas, above the current page
                .onLoad(null) // called after document is loaded and starts to be rendered
                .onPageChange(null)
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e("PDF Error", t.getMessage()+"");
                    }
                }) // called after document is rendered for the first time
                .enableAnnotationRendering(true) // render annotations (such as comments, colors or forms)
                .password(null)// improve rendering a little bit on low-res screens
                .load();
        return view;
    }

    private void getCurrentSlide() {
        switch (currentSlide){
            case VUR:
                currentPDF=new File(getActivity().getExternalFilesDir(null), MainActivity.VUR_REPORT);
                break;
            case SUMMARY:
                currentPDF=new File(getActivity().getExternalFilesDir(null), MainActivity.SUMMARY_REPORT);
                break;
            case ROB:
                currentPDF=new File(getActivity().getExternalFilesDir(null), MainActivity.ROB_REPORT);
                break;
            case VEF:
                currentPDF=new File(getActivity().getExternalFilesDir(null), MainActivity.VEF_REPORT);
                break;
        }
    }

}
