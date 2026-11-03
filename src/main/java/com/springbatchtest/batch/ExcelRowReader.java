package com.springbatchtest.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamException;
import org.springframework.batch.infrastructure.item.ItemStreamReader;

public class ExcelRowReader implements ItemStreamReader<Row> {

    private final String filePath; // 엑셀 파일 경로
    private FileInputStream fileInputStream; // 해당 경로를 기반으로 파일을 여는 객체
    private Workbook workbook; // 열어본 데이터를 적는 객체
    private Iterator<Row> rowCursor; // 반복 객체
    private int currentRowNumber; // 반복 시점 상태
    private final String CURRENT_ROW_KEY = "current.row.number";

    public ExcelRowReader(String filePath) throws IOException {
        this.filePath = filePath;
        this.currentRowNumber = 0;
    }

    // 최초 1회 실행
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            this.rowCursor = sheet.iterator();

            // 동일 배치 파라미터에 대해 특정 키 값 "current.row.number"의 값이 존재한다면 초기화
            if (executionContext.containsKey(CURRENT_ROW_KEY)) {
                currentRowNumber = executionContext.getInt(CURRENT_ROW_KEY);
            }

            // 위 값을 가져와 이미 실행한 부분은 건너 뜀
            for (int i = 0; i < currentRowNumber && rowCursor.hasNext(); i++) {
                rowCursor.next();
            }
        } catch (IOException e) {
            throw new ItemStreamException(e);
        }
    }

    @Override
    public @Nullable Row read() throws Exception {
        if (rowCursor != null && rowCursor.hasNext()) {
            currentRowNumber++;
            return rowCursor.next();
        } else {
            return null;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT_ROW_KEY, currentRowNumber);
    }

    // 마지막 1회 실행
    @Override
    public void close() throws ItemStreamException {
        try {
            if (workbook != null) workbook.close();
            if (fileInputStream != null) fileInputStream.close();
        } catch (IOException e) {
            throw new ItemStreamException(e);
        }
    }
}
