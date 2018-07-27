//package com.myke.test;
//
//import com.google.gson.Gson;
//import com.jd.ecc.console.zuul.ip.config.Policy;
//import com.jd.ecc.console.zuul.ip.config.PolicyType;
//import com.jd.ecc.console.zuul.ip.config.user.UserAccessControl;
//import org.junit.Test;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class UsterTest {
//
//    @Test
//    public void listTest() {
//        List<User> users = new ArrayList<>();
//        users.add(new User("1", 2));
//        users.add(new User("2", 20));
//
//        List<Address> addresses = new ArrayList<>();
//        addresses.add(new Address("1", "bj"));
//        addresses.add(new Address("4", "sh"));
//
//        List<String> usersId = users.stream()
//                .map(user -> user.getId())
//                .collect(Collectors.toList());
//
//        List<Address> addressList = addresses.stream().
//                filter(address -> usersId.contains(address.getId()))
//                .collect(Collectors.toList());
//        for (Address address : addressList) {
//            System.out.println(address);
//        }
//
//    }
//
//    @Test
//    public void userTest() {
//        Gson gson = new Gson();
//        Map<Long, UserAccessControl.UserPolicy> userPolicy = new HashMap<>();
//        UserAccessControl.UserPolicy userAccessControl = new UserAccessControl.UserPolicy();
//        Policy policy = new Policy();
//        policy.setPolicyDesc("order-service");
//        policy.setPolicyType(PolicyType.SERVICE);
//        userAccessControl.getPolicies().put("10.12.221.0", Arrays.asList(policy));
//        userPolicy.put(2L, userAccessControl);
//        System.out.println(gson.toJson(userPolicy));
//    }
//}
