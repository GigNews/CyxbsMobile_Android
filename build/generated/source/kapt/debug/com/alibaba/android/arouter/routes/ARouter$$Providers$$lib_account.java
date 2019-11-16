package com.alibaba.android.arouter.routes;

import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IProviderGroup;
import com.mredrock.cyxbs.account.AccountService;
import java.lang.Override;
import java.lang.String;
import java.util.Map;

/**
 * DO NOT EDIT THIS FILE!!! IT WAS GENERATED BY AROUTER. */
public class ARouter$$Providers$$lib_account implements IProviderGroup {
  @Override
  public void loadInto(Map<String, RouteMeta> providers) {
    providers.put("com.mredrock.cyxbs.common.service.account.IAccountService", RouteMeta.build(RouteType.PROVIDER, AccountService.class, "/account/service", "account", null, -1, -2147483648));
  }
}
