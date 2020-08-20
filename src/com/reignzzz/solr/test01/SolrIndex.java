package com.reignzzz.solr.test01;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolrIndex {

    public static void main(String[] args) throws IOException, SolrServerException {
//        insertOrUpdateIndex();
//        deleteIndex();
        simpleSearch();
    }


    public static void insertOrUpdateIndex() throws IOException, SolrServerException {
        // solr服务的url，tb_item是前面创建的solr core
        String url = "http://192.168.81.129:8080/solr/db_doc_core";
        // 创建HttpSolrClient
        HttpSolrClient client = new HttpSolrClient.Builder(url)
                .withConnectionTimeout(5000)
                .withSocketTimeout(5000)
                .build();

        // 创建Document对象
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "1111");
        document.addField("title", "Solr入门");
        document.addField("sellPoint", "Solr版本差异也太大了");

        client.add(document);

        client.commit();
    }

    public static void deleteIndex() throws IOException, SolrServerException {
        String url = "http://192.168.81.129:8080/solr/db_doc_core";
        HttpSolrClient client = new HttpSolrClient.Builder(url)
                .withConnectionTimeout(5000)
                .withSocketTimeout(5000)
                .build();
        client.deleteById("1111");
        client.commit();
    }

    public static void simpleSearch() throws IOException, SolrServerException {
        String url = "http://192.168.81.129:8080/solr/db_doc_core";
        HttpSolrClient client = new HttpSolrClient.Builder(url)
                .withConnectionTimeout(5000)
                .withSocketTimeout(5000)
                .build();
        // 创建SolrQuery
        SolrQuery query = new SolrQuery();
        // 输入查询条件
        query.setQuery("pathftype:pdf");
        // 执行查询并返回结果
        QueryResponse response = client.query(query);
        // 获取匹配的所有结果
        SolrDocumentList list = response.getResults();
        // 匹配结果总数
        long count = list.getNumFound();
        System.out.println("总结果数：" + count);

        for (SolrDocument document : list) {
            System.out.println(document.get("id"));
            System.out.println(document.get("created"));
            System.out.println("================");
        }

    }

    /**
     * q - 查询关键字，必须的，如果查询所有使用*:*。请求的q是字符串；
     * fq - (filter query)过虑查询，在q查询符合结果中同时是fq查询符合的。例如：请求fq是一个数组（多个值）；
     * sort - 排序；
     * start - 分页显示使用，开始记录下标，从0开始；
     * rows - 指定返回结果最多有多少条记录，配合start来实现分页；
     * fl - 指定返回那些字段内容，用逗号或空格分隔多个 ；
     * df-指定一个搜索Field；
     * wt - (writer type)指定输出格式，可以有 xml, json, php, phps；
     * hl 是否高亮，设置高亮Field，设置格式前缀和后缀。
     * facet 查询分组的数量（可与查询同步执行）
     * group 查询每个分组前几条的数据
     */

    public void complexSearch() throws IOException, SolrServerException {
        String url = "http://192.168.81.129:8080/solr/db_doc_core";
        HttpSolrClient client = new HttpSolrClient.Builder(url)
                .withConnectionTimeout(5000)
                .withSocketTimeout(5000)
                .build();
        SolrQuery query = new SolrQuery();
        // 输入查询条件
        query.setQuery("title:手机 AND sellPoint:移动");
        // 设置过滤条件
        query.setFilterQueries("id:[1000000 TO 1200000]");
        // 设置排序
        query.addSort("id", SolrQuery.ORDER.desc);
        // 设置分页信息（使用默认的）
        query.setStart(2);
        query.setRows(2);
        // 设置显示的Field的域集合(两种方式二选一)
        // query.setFields(new String[]{"id", "title", "sellPoint", "price", "status" });
        query.setFields("id,title,sellPoint,price,status");
        // 设置默认域
        // query.set("df", "product_keywords");
        // 设置高亮信息
        query.setHighlight(true);
        query.addHighlightField("title");
        query.setHighlightSimplePre("<span color='red'>");
        query.setHighlightSimplePost("</span>");

        // 执行查询并返回结果
        QueryResponse response = client.query(query);
        // 获取匹配的所有结果
        SolrDocumentList list = response.getResults();
        // 匹配结果总数
        long count = list.getNumFound();
        System.out.println("总结果数：" + count);

        // 获取高亮显示信息
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        for (SolrDocument document : list) {
            System.out.println(document.get("id"));
            List<String> list2 = highlighting.get(document.get("id")).get("title");
            if (list2 != null)
                System.out.println("高亮显示的商品名称：" + list2.get(0));
            else {
                System.out.println(document.get("title"));
            }

        }
    }

//    分组查询
    /*solrQuery.setParam(GroupParams.GROUP,true);
solrQuery.setParam(GroupParams.GROUP_FIELD,"id");
// 设置每个quality对应的
solrQuery.setParam(GroupParams.GROUP_LIMIT,"1");

    GroupResponse groupResponse =queryResponse.getGroupResponse();
if(groupResponse !=null) {
        List<GroupCommand> groupList =groupResponse.getValues();
        for(GroupCommand groupCommand : groupList){
            List<Group> groups =groupCommand.getValues();
            for(Group group : groups) {
                System.out.println(group.getGroupValue()+"\t"+group.getResult().getNumFound());
            }
        }
    }*/

    //查询分组数量
/*solrQuery.setFacet(true);
solrQuery.setFacetLimit(100);
solrQuery.setFacetMissing(false);
solrQuery.addFacetField("id");

    List<FacetField.Count> counts;
    List<FacetField> facetFieldList = queryResponse.getFacetFields();
for (FacetField facetField : facetFieldList) {
        System.out.println(facetField.getName()+"\t"+facetField.getValueCount());
        counts = facetField.getValues();
        if (counts != null) {
            for (FacetField.Count count : counts) {
                System.out.println(count.getName()+" "+count.getCount());
            }
        }
    }*/



}
