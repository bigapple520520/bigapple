/* 
 * @(#)HttpClient.java    Created on 2015-3-23
 * Copyright (c) 2015 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http2;

/**
 * Http访问接口，外部使用
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2015-3-23 下午5:01:10 $
 */
public interface HttpClient {
    String get(String url, RequestParams requestParams);
}
