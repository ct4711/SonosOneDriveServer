package me.michaeldick.sonosonedrive.model;

import javax.sound.midi.Track;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Item {
    public enum FileType {
        file,
        audio,
        folder
    };

    FileType type;

    String id;
    String name;
    String mimeType;
    int duration;
    String album;
    String artist;
    String title;
    String parentId;
    String fileUri;
    String thumbnail;
    int track;
    int childCount;

    public int getChildCount() {
        return childCount;
    }

    public int getTrack() {
        return track;
    }

    public Item(JsonObject data) {
        id = data.has("id") ? data.get("id").getAsString() : null;
        name = data.has("name") ? data.get("name").getAsString() : null;

        if (data.has("parentReference")) {
            JsonObject parentAttributes = data.getAsJsonObject("parentReference");
            parentId = parentAttributes.has("parentId") ? parentAttributes.get("parentId").getAsString() : null;
        }

        if (data.has("file")) {
            JsonObject fileAttributes = data.getAsJsonObject("file");
            mimeType = fileAttributes.has("mimeType") ? fileAttributes.get("mimeType").getAsString() : null;
            fileUri = data.has("@microsoft.graph.downloadUrl") ? data.get("@microsoft.graph.downloadUrl").getAsString() : null;
            switch (mimeType) {
                case "audio/mpeg":
                case "audio/mp4":
                case "audio/x-ms-wma":
                case "audio/wav":
                    type = FileType.audio;
                    title = data.has("name") ? getWithoutExtension(name) : null;
                    break;
                case "video/mp4":
                    mimeType = "audio/mp4";
                    title = data.has("name") ? getWithoutExtension(name) : null;
                    type = FileType.audio;
                    break;
                case "audio/x-flac":
                    mimeType = "audio/flac";
                    title = data.has("name") ? getWithoutExtension(name) : null;
                    type = FileType.audio;
                    break;
                case "application/octet-stream":
                    if (data.has("name") && getExtension(name).equals("ogg")) {
                        mimeType = "application/ogg";
                        title = data.has("name") ? getWithoutExtension(name) : null;
                        type = FileType.audio;
                    } else {
                        type = FileType.file;
                    }
                    break;
                default:
                    type = FileType.file;
                    break;
            }
        } else if (data.has("folder")) {
            type = FileType.folder;
            JsonObject folderAttributes = data.getAsJsonObject("folder");
            childCount = folderAttributes.has("childCount") ? folderAttributes.get("childCount").getAsInt() : 0;
        }

        if (data.has("thumbnails")) {
            JsonArray thumbnails = data.getAsJsonArray("thumbnails");
            if (thumbnails.size() > 0) {
                thumbnail = thumbnails.get(0).getAsJsonObject().getAsJsonObject("small").get("url").getAsString();
            }
        }
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public FileType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getDuration() {
        return duration;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getParentId() {
        return parentId;
    }

    public String getFileUri() {
        return fileUri;
    }

    public static String getExtension(String fileName) {
        char ch;
        int len;
        if (fileName == null ||
            (len = fileName.length()) == 0 ||
            (ch = fileName.charAt(len - 1)) == '/' || ch == '\\' || //in the case of a directory
            ch == '.') //in the case of . or ..
            return "";
        int dotInd = fileName.lastIndexOf('.'),
            sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (dotInd <= sepInd)
            return "";
        else
            return fileName.substring(dotInd + 1).toLowerCase();
    }

    public String getWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}