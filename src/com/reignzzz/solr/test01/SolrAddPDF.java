package com.reignzzz.solr.test01;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest.ACTION;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;

public class SolrAddPDF {
    public static void main(String[] args) {
        String fileName = "D://temp//自建的pdf.pdf";
        String solrId = "自建的pdf.pdf";
// String path = "";
        try {
// indexFilesSolrCell(solrId, solrId,fileName);
            indexFilesSolrCell(fileName, solrId, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

    private static String GetCurrentDate() {
        Date dt = new Date();
        //最后的aa表示“上午”或“下午” HH表示24小时制 如果换成hh表示12小时制
// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(dt);
        return day;
    }

    public static void indexFilesSolrCell(String fileName, String solrId, String path)
            throws IOException, SolrServerException {
        String urlString = "http://192.168.81.129:8080/solr/db_doc_core";
        SolrClient solr = new HttpSolrClient.Builder(urlString).build();
        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");
        String contentType = getFileContentType(fileName);
        up.addFile(new File(path), contentType);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        up.setParam("literal.id", fileName);
        up.setParam("literal.path", path);//文件路径
        up.setParam("literal.pathuploaddate", GetCurrentDate());//文件上传时间
        up.setParam("literal.pathftype", fileType);//文件类型，doc,pdf
        up.setParam("fmap.content", "attr_content");//文件内容
        up.setAction(ACTION.COMMIT, true, true);
        solr.request(up);
    }

    /**
     * @Author：sks
     * @Description：根据文件名获取文件的ContentType类型
     * @Date：
     */
    public static String getFileContentType(String filename) {
        String contentType = "";
        String prefix = filename.substring(filename.lastIndexOf(".") + 1);
        if (prefix.equals("xlsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (prefix.equals("pdf")) {
            contentType = "application/pdf";
        } else if (prefix.equals("doc")) {
            contentType = "application/msword";
        } else if (prefix.equals("txt")) {
            contentType = "text/plain";
        } else if (prefix.equals("xls")) {
            contentType = "application/vnd.ms-excel";
        } else if (prefix.equals("docx")) {
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (prefix.equals("ppt")) {
            contentType = "application/vnd.ms-powerpoint";
        } else if (prefix.equals("pptx")) {
            contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else {
            contentType = "othertype";
        }
        return contentType;
    }

}
