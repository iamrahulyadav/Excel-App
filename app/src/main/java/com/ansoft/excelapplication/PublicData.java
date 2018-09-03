package com.ansoft.excelapplication;

import com.aspose.cells.Workbook;

/**
 * Created by Bibek on 4/8/2017.
 */

public class PublicData {

    static Workbook workbook;

    public static Workbook getWorkbook() {
        return workbook;
    }

    public static void setWorkbook(Workbook workbook) {
        PublicData.workbook = workbook;
    }
}
