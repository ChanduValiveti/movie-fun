package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import java.io.*;
import java.util.Optional;



public class FileStore implements BlobStore {

    private Tika tika = new Tika();

    @Override
    public void put(Blob blob) throws IOException {
        File coverFile = new File("covers/"+blob.name);
        coverFile.delete();
        coverFile.getParentFile().mkdir();
        coverFile.createNewFile();

        try(FileOutputStream outputStream = new FileOutputStream(coverFile)){
            IOUtils.copy(blob.inputStream,outputStream);
        }

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        File file = new File("covers/"+name);

        if(!file.exists())
        {
            return Optional.empty();
        }

        return Optional.of(new Blob(name,new FileInputStream(file),tika.detect(file)));

    }

    @Override
    public void deleteAll() {

    }

}