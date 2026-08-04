package org.mediaplayer.android;

import android.content.Context;
import android.util.Log;

import org.mediaplayer.core.Generics.GenericFileLogic;
import org.mediaplayer.core.TrackEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Tools.Files.AndroidFileTree;
import Tools.Files.Data.DataTypes.DataString;
import Tools.Files.Data.Exceptions.DataTypeException;
import Tools.Files.GenericFileManager;
import Tools.Files.GenericFileTree;
import Tools.Files.Util;

public class AndroidFileLogic extends GenericFileLogic {
    private Context context;
    public AndroidFileLogic(Context context, Path path) throws IOException {
        super(path);
        this.context = context;
    }

    public void create(AndroidAudio audio) throws IOException, DataTypeException {
        this.audio = audio;
        super.create();
    }

    @Override
    public GenericFileTree createFileTree() throws IOException {
        return new AndroidFileTree(context, path);
    }

    @Override
    public void correctPathData(Map<String, DataString> map) {
        map.forEach((id, data) -> {
            data.setData(data.get().replace("\\", "/"));
        });
    }
}
