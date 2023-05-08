package com.bebracore.productswatching;

import com.bebracore.productswatching.service.FetchingProductsService;
import com.bebracore.productswatching.service.InfoType;

import parser.Type;

public class Test {
	public static void main(String[] args) {
		FetchingProductsService serv = new FetchingProductsService();
		serv.fetchResources("https://www.mvideo.ru/bff/products/listing", Type.MVIDEO, InfoType.LONG);
	}
}
