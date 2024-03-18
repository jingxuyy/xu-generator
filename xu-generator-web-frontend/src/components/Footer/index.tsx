import { GithubOutlined } from '@ant-design/icons';
import { WeiboOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = '程序员xu';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'bilibili',
          title: 'bilibili',
          href: 'https://space.bilibili.com/314872764?spm_id_from=333.1007.0.0',
          blankTarget: true,
        },
        {
          key: 'weibo',
          title: (
            <>
              <WeiboOutlined /> 微博
            </>
            ),
          href: 'https://www.weibo.com/',
          blankTarget: true,
        },
        {
          key: 'github',
          title: (
            <>
              <GithubOutlined /> 源码
            </>
          ),
          href: 'https://github.com/jingxuyy',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
