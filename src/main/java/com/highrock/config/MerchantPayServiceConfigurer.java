package com.highrock.config;

import com.highrock.handlers.AliPayMessageHandler;
import com.highrock.handlers.WxPayMessageHandler;
import com.highrock.interceptor.AliPayMessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Configuration;

import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.spring.boot.core.PayServiceConfigurer;
import com.egzosn.pay.spring.boot.core.configurers.MerchantDetailsServiceConfigurer;
import com.egzosn.pay.spring.boot.core.configurers.PayMessageConfigurer;
import com.egzosn.pay.spring.boot.core.merchant.PaymentPlatform;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.AliPaymentPlatform;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.PaymentPlatforms;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.WxPaymentPlatform;


/**
 * 支付服务配置
 *
 * @author egan
 *         email egzosn@gmail.com
 *         date 2019/5/26.19:25
 */
@Configuration
public class MerchantPayServiceConfigurer implements PayServiceConfigurer {
    @Autowired
    private AutowireCapableBeanFactory spring;
    @Autowired
    private AliPayMessageHandler aliPayMessageHandler;
    @Autowired
    private WxPayMessageHandler wxPayMessageHandler;
    
    //微信公众号appid
	public static final String wxAppId = "";
	//微信公众号秘钥
	public static final String wxAppSecret = "";
    
    /**
     * 商户配置
     *
     * @param merchants 商户配置
     */
    @Override
    public void configure(MerchantDetailsServiceConfigurer merchants)  {
//        数据库文件存放 /doc/sql目录下
//        merchants.jdbc().template(jdbcTemplate);
        //微信请求配置，详情参考https://gitee.com/egzosn/pay-java-parent项目中的使用
        HttpConfigStorage wxHttpConfigStorage = new HttpConfigStorage();
//        wxHttpConfigStorage.setKeystore("http://www.egzosn.com/certs/ssl 退款证书");
//        wxHttpConfigStorage.setCertStoreType(CertStoreType.URL);
//        wxHttpConfigStorage.setStorePassword("ssl 证书对应的密码， 默认为商户号");
        //内存Builder方式
        merchants.inMemory()
                .ali()
                .detailsId("1")
//                .appid("2021001166680334")
                .appid("2016102900777639")
                //应用私钥
//                .keyPrivate("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCsrlJ3E09rODwhRdLI1O53Z7x3HAgadqkkNwmAH2DGGQhywsoLXLmc3QzjGcPE7oKTPLtT93QsMNRBqTr3I8LSqvU30YPNqDdfXQGK1WEsMgPMnNYNc2/TjHqbUZxk78gaDkc7/TDlKQ8yzppFDja4/BuH0WZSBxMbeYa3IEb7N9Yry1ij/2LITTW4wv6DdzWM85jOBpS+2782XeXZDRp7oOY+x5aIi/EWBbeSuB2w2szDPphV62Q0EL9wSMvhu/cF5FfPtAZU733KiWr2kWORIqJR7EaI0L89RA8qASK5QmglxUltkvV1w91Ac3XpgPqD9hPWpwNhjUBVGxlUnU3NAgMBAAECggEAX/5IOYmyjJgmVRjLpHk6V4aDbnNWzd9JB6jQzziiQ32HBMTkLIzPYjRABb885SzxyYfpWzQkkKxUUmwRK3Hly9bdmroN2zW4EntRcCxOaJMlKYnGNJQHhkKBpfYoSrRBOLrXPrHxjHLW0BeqCtlr8kmzxDzT9KWaKZvVF9gJnXzy/CP9/j3vhDjSJviWnXTjAp3s8lh8lvEy4LGGUMwLHnWOienVsa6dQ/iXqHxmWnZBMsvhU0e5G+IcBul40YonyzvLUvkfGKV+X2GhfBFxY9c69j/k/edb/9jie4l2YC81JHDjyi+msgt0RJgwHCJKOgRtKpi3C4N+bsQ9kGkEwQKBgQDUUq/YQKMDvUcMnxNCKnfEpuX+60pPjn9IOtyOn+MzSC1V3i4Q6I45Fz5ErtcwiJAlh/AXLya0urBJxG8pmwQdJoKPxrGbbLZl9VI0N2WIIzidxjIq9QVLFM5JFyY4wh4JExznZ9zaat8XXu4GwCHF3vOjJYK2bvov1M58mQH4zwKBgQDQNANe3mLoamraEIEYRZRLEeTK5xT747NNmGy3ju4eqrvDpgWRkutJr0A6VLp75L7vWOVa0bVrp4GSDWZItKcImuNAys4frhCukL8iTQkicnaP3NL85lfOuZIzqRaZn6IkPz/g/SSBHad0e3X/mh721bNaNo2ied1PVThmnzp+owKBgDHcEQs5co6IPk0Kdh1Nl76Iau5IFrVEA0PU/sJHbbU3N944RJ0X334spSS/4iy1hRQrLghjvDUougk4pdohWv/1cSeOQ1r6VoLu7xTwBVnI25zBVDqeO1X8Vsz3MLSXcTI0WM9Gpqhr/GOimUbgi43J43DmeCGbQglFcg/0ZnAlAoGANNWuMUnwAZjXysS7beRiUHaW/+KKUa2j18IhfKdZyBB9m5Wq/hkxej5t5x+PP1d6GJUPhu+GwW39lImv9i1Z4fYkS1Hr4uAyPYpf5p1z/vjbG4DnTeRKThyLfG6hXJTE+bvPQxIaTGWxE/8sn5OF3g+29uK3KI8SWUqACJLJrk8CgYAwL/5+CsAcZ5jIbuqQat6rSP1/7WJh33v4yRTYpfQhE9TKit562ilSHb5MKhvKbKQsW5zRc/41VfGC34Owuu/DMD05dXkqHQnzkQsJBcWMTyhSk14cOphfMQJSzbydBCtmDGnDabSRq2Rmd+G3DLxzsRa8IJuu4qhPdbTtfA28zg==")
                .keyPrivate("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCUjzJR3fkX1Tp1OWzC2x293f+951dPwI50bxwiJmEOVUFx7nIkT/Y9z9TZXGL1YktvBVDYcK/r2yDg/D9sEw3S3iY1K9pCnh9HoIZWhJCwUHRydx4wo9h19vz0pjipzFC7RAyGcKCiLNEiGiR8kJbXZLEJlIrR2L2zJwGQCWVx4k4TFrBncwsyEgCVNJwTBK3Yd9mG7dr41ohE67m4+ASyYwHsxwel0SXg9lCVJ2HRTU/3GnCf0f/6ms4FuFTQHvNda49u9OwmR+ZGfdBBVhPeq3Mi73a0wcAWxk7xoKxcnjdTvIhZiNGwJ7AcOsZfdY5QQe1etR+3LTOCJX3/J841AgMBAAECggEAR2LYB0bm33Dy+klx3eB01jvLHHICHqykx4Zsumw+zyhgnmK6LoDODr3Dxf8C/m8RWHPxeVAM+QT/M52CzGgLiHx/V6WO59wbo8ubQg/Jhy/iBO8AbtcMuQLKYrVNHSq00IfOjPup9MQcaoqtZb4E/dPAzp22Z0gDQAdKVPBu8t3TtrUrvfjR7C+eN4rWyhXhcR56RH7c9mf422azSdVI08eeHBWsCc6Y13xhBV6Rs5WQ0TVel0v9Z+Uve1LQsFJYzv7W6Axlcyg3RCUsM+BqmjbUZJglUk2URpUbROfMV4HvnlzT2dKpfXhSFt3YvQfLcvLQ8QLEsYvgt8FEDsu7MQKBgQDYvWRaa/fneCAgPMEHJ1GnW6Nw7hYToOQJv+UVMvBlURB5Z1NRYBuwr9sVJUUEoH9H8iFdGLnNh15A6GDSJJsUx61Alztg6c6XQZNYoTamdur0PNJhBRYfSqr+86oZa8vHEssPsAGIDCZ6x1UZPq34PaA5K8bsHpDiRH7zaJIRQwKBgQCveCgW8L8AcCcu8ajCXu5nXIqzCPMUl3nuHsxzB5HTyaF3nYQIvNOkz6U6DqGyZeji7axv7u4l3gf6RHCtiIkYa5ryicoEddGirjEOn0W2FxFPTMNKu5SNnIY17iKn4wRCqd6PuigFUrMTU4gyB+aBVnEoem+aJk/BfV29NybPJwKBgDkFitNnOG+DYSBaG4agR5E6QMPDP7emSphuDf0Hy8s8CpOGJZtxJ5fSjFcB2SigeRwajcCKcsep55g4CtD+pHZRGjm2D/8tRBbzP6q/rzsYajQblFXGGs3cvSQaQxQxEgmr3hSgf6alW7JNowvjO63luLOR2uHgIMoGr0emqVc1AoGBAIFXzA1k9xZmlauX/TMR8SCF6cOhZDWYlwt87m4YdVktdvWKScp7DnOdkIjkQjMTxgCI/CQidq+5JB5KFtkEHTLAFx6TjTOOebI2PvktVKCuDldrGUXcUdu1NoVIfYlN4VUWjHVb3qZV2KT1XR6sDjDmvFD/N4bkmMPjGpcgsV3fAoGAGA5E/K+y5JvxFCQTgqarkJC2nKqm56E9Zd+oxq0qGuSVK68C1FQs+yqmdq0Ri1h03RBXZJX+O/rvQtbjMsDtYZWR0e+Bxw1iXEfV8QSqzeTeb4EsWkMedZg4Hvtx6O+TarH5q0iWWdmelCCSEEr8BX3Xals5QcMjksh0Fwe8lr0=")
                //支付宝公钥
//                .keyPublic("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs88ZI92REyYsSq82/qiYSohS/BGtUWMvvcTNzv0cCtgPy/hHMg96vsGfYxXS9pa4QWKr3yE7UcqlWxJLMkI3U76D9miNhBalO9zsWCLM+T2dEBvtExsxSiaV7g/LhaIDPeRs6wZ/a3ceP9uLO2EKDBu4+KpGnvNxIlU5qVa1fw3SkXbk488dl0cek5fUkkZdmWLQQHDidQ2Jtpe+qZ/a3aijCi7vXyyNWFy7kB8YWEIwa3Qfbs6czgK3i0kfjHWVguj8l73jzbn/AdF6VKcd3o5O24eds/3Hwu57bckJHcf3avlZRxsv9RSWiid9KEIJ0adRgMgQsvA3XPmY3FJcCwIDAQAB")
                .keyPublic("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgSvhVdTxFw6OtnFxQX+WoIgqlkiMolb9GLZszBlicyB7nKhd+f+1qhRx6gxvKkos+GjlMtF3cY14y9CtyA12NeGbr1jn491H+Qpha4WYN2KuA3m+xE7fUWDOmENY9TJaOyNoTXfFTU8R8tAha8LvJoIc5/Y9+fYRUXRJwbiM3BbnFxVzk8eZm0ZsqX+Jl+J+GcalRXBslHDPAwTQdH5eS3DXilHU6V5t8MCLArWNGz18IsdtKrgXstP4chNHdTCIRB/3zxshMadH0HCdyn/D++YqPrHwEHZ/OtnkrVdNtXI+0Nxi++UZ72R8B2vNvEENC5M5LrIDmJJ7gwT+bmhUXwIDAQAB")
                .inputCharset("utf-8")
                .notifyUrl("http://192.168.31.217:8080/PayDemo/payBack1.json")
                .returnUrl("http://192.168.31.217:8080/PayDemo/payBack1.json")
//                .pid("2088831671105662")
//                .seller("2088831671105662")
                .signType("RSA2")
                .test(true)
                .and()
                .wx()
                .detailsId("2")
                .appid("公众号id")
                .mchId("商户id")
                .secretKey("商户秘钥")
                .notifyUrl("http://pay.egzosn.com/PayDemo/payBack2.json")
                .returnUrl("http://pay.egzosn.com/PayDemo/payBack2.json")
                .inputCharset("utf-8")
                .signType("MD5")
                //设置请求相关的配置
                .httpConfigStorage(wxHttpConfigStorage)
                .test(false)
                .and()
        ;

      /*  //------------内存手动方式------------------
        UnionMerchantDetails unionMerchantDetails = new UnionMerchantDetails();
        unionMerchantDetails.detailsId("3");
        //内存方式的时候这个必须设置
        unionMerchantDetails.setCertSign(true);
        unionMerchantDetails.setMerId("700000000000001");
        //公钥，验签证书链格式： 中级证书路径;
        unionMerchantDetails.setAcpMiddleCert("D:/certs/acp_test_middle.cer");
        //公钥，根证书路径
        unionMerchantDetails.setAcpRootCert("D:/certs/acp_test_root.cer");
        //私钥, 私钥证书格式： 私钥证书路径
        unionMerchantDetails.setKeyPrivateCert("D:/certs/acp_test_sign.pfx");
        //私钥证书对应的密码
        unionMerchantDetails.setKeyPrivateCertPwd("000000");
        //证书的存储方式
        unionMerchantDetails.setCertStoreType(CertStoreType.PATH);
        unionMerchantDetails.setNotifyUrl("http://127.0.0.1/payBack4.json");
        // 无需同步回调可不填  app填这个就可以
        unionMerchantDetails.setReturnUrl("http://127.0.0.1/payBack4.json");
        unionMerchantDetails.setInputCharset("UTF-8");
        unionMerchantDetails.setSignType("RSA2");
        unionMerchantDetails.setTest(true);
        //手动加入商户容器中
        merchants.inMemory().addMerchantDetails(unionMerchantDetails);*/
    }
    /**
     * 商户配置
     *
     * @param configurer 支付消息配置
     */
    @Override
    public void configure(PayMessageConfigurer configurer) {
        PaymentPlatform aliPaymentPlatform = PaymentPlatforms.getPaymentPlatform(AliPaymentPlatform.platformName);
        configurer.addHandler(aliPaymentPlatform, aliPayMessageHandler);
        configurer.addInterceptor(aliPaymentPlatform, spring.getBean(AliPayMessageInterceptor.class));
        configurer.addHandler(PaymentPlatforms.getPaymentPlatform(WxPaymentPlatform.platformName),wxPayMessageHandler);
    }
}
