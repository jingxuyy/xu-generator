import {PageContainer, ProFormText} from '@ant-design/pro-components';
import React, {useEffect, useRef, useState} from 'react';
import {listGeneratorVoByPage} from "@/services/backend/generatorController";
import {ProForm, ProFormSelect, QueryFilter} from "@ant-design/pro-form/lib";
import {Avatar, Card, Flex, Image, Input, List, message, Space, Tabs, Tag, Typography} from "antd";
import moment from "moment";
import {UserOutlined} from "@ant-design/icons";

const DEFAULT_PAGE_PARAMS: PageRequest = {
  current:1,
  pageSize:4,
  sortField: 'createTime',
  sortOrder: 'descend',
}

const IndexPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [total, setTotal] = useState<number>(0);
  const [dataList, setDataList] = useState<API.GeneratorVO[]>([])

  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>(
    {
      ...DEFAULT_PAGE_PARAMS,
    }
  );

  const doSearch=async ()=>{
    setLoading(true);
    try {
      const res = await listGeneratorVoByPage(searchParams);
      setDataList(res.data?.records ?? []);
      setTotal(Number(res.data?.total) ?? 0);
    }catch (error:any){
      message.error("获取数据失败，"+error.m);
    }
    setLoading(false);
  };
  useEffect(()=>{
    doSearch();
  }, [searchParams]);

const tagListView=(tags?:string[]) => {
  if(!tags){
    return <></>
  }
  return (
    <div style={{marginBottom:8}}>
      {tags.map((tag)=>(
      <Tag key={tag}>{tag}</Tag>
      ))}
    </div>

  );
};

  // @ts-ignore
  return (
    <PageContainer title={<></>}>
      <Flex justify={"center"}>
        <Input.Search
          style={{
            width:'40vw',
            minWidth:320,
          }}
          placeholder="请搜索生成器"
          allowClear
          enterButton="搜索"
          size="large"
          onChange={(e)=>{
            searchParams.searchText=e.target.value;
          }}
          onSearch={(value:string)=>{
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS,
              searchText:value,
            })
          }}
        />
      </Flex>
        <div style={{marginBottom:16}}/>

    <Tabs
      size={"large"}
      defaultActiveKey={"newest"}
      items={[
        {
          key:'newest',
          label:'最新',
        },
        {
          key:'recommend',
          label:'推荐',
        },
      ]}
      onChange={()=>{}}
      />

      <QueryFilter collapsed={false}
        span={12}
        labelWidth={"auto"}
        labelAlign={"left"}
        style={{padding: '16px 0'}}
        onFinish={async (values:API.GeneratorQueryRequest)=>{
          setSearchParams({
            ...DEFAULT_PAGE_PARAMS,
            // @ts-ignore
            ...values,
            searchText:searchParams.searchText
          });
        }}
      >
        <ProFormText name={"name"} label={"名称"}/>
        <ProFormSelect name={"tags"} label="标签" mode={"tags"}/>
        <ProFormText name={"description"} label={"描述"}/>
      </QueryFilter>

        <div style={{marginBottom:24}}/>

      <List<API.GeneratorVO>
        rowKey={"id"}
        loading={loading}
        grid={{
          gutter:16,
          xs:1,
          sm:2,
          md:3,
          lg:3,
          xl:4,
          xxl:4,
        }}
        dataSource={dataList}
        pagination={{
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total,
          onChange(current:number, pageSize:number) {
            setSearchParams({
              ...searchParams,
              current,
              pageSize,
            });
          },
        }}
        renderItem={(data )=> (
          <List.Item>
            <Card hoverable cover={<Image alt={data.name} src={data.picture} style={{height:200}}/>}>
              <Card.Meta
                title={<a>{data.name}</a>}
                description={
                  <Typography.Paragraph ellipsis={{rows:2}} style={{height:44}}>
                    {data.description}
                  </Typography.Paragraph>
                }
              />
              {tagListView(data.tags)}
              <Flex justify={"space-between"} align={"center"}>
                <Typography.Paragraph  type={"secondary"} style={{fontSize:12}}>
                  {moment(data.createTime).fromNow()}
                </Typography.Paragraph>
                <div>
                  <Avatar src={data.user?.userAvatar ?? <UserOutlined />}/>
                </div>
              </Flex>
            </Card>
          </List.Item>
          )}
        />
    </PageContainer>
  );
};

export default IndexPage;
