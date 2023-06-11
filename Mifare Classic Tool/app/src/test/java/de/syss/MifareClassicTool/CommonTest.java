package de.syss.MifareClassicTool;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommonTest {
    private Context context;
    private ContentResolver contentResolver;

    @Before
    public void setUp() {
        context = mock(Context.class);
        contentResolver = mock(ContentResolver.class);
        when(context.getContentResolver()).thenReturn(contentResolver);
    }

    @Test
    public void testGetFileName_withContentScheme() {
        Uri uri = mock(Uri.class);
        when(uri.getScheme()).thenReturn("content");

        Cursor cursor = mock(Cursor.class);
        when(contentResolver.query(uri, null, null, null, null)).thenReturn(cursor);
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).thenReturn(0);
        when(cursor.getString(0)).thenReturn("example.txt");

        String fileName = getFileName(uri, context);

        assertEquals("example.txt", fileName);
    }

    @Test
    public void testGetFileName_withFilePath() {
        Uri uri = mock(Uri.class);
        when(uri.getScheme()).thenReturn("file");
        when(uri.getPath()).thenReturn("/storage/emulated/0/example.txt");

        String fileName = getFileName(uri, context);

        assertEquals("example.txt", fileName);
    }

    private String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(
                uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
