package com.blazebit.domain.declarative.impl.spi;

import com.blazebit.domain.Domain;
import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.declarative.DeclarativeDomain;
import com.blazebit.domain.declarative.DeclarativeDomainConfiguration;
import com.blazebit.domain.declarative.DomainFunctionParam;
import com.blazebit.domain.declarative.DomainFunctions;
import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.DomainModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class DeclarativeDomainTest {

    @DomainFunctions
    public static class TestFunctions {
        public static Integer size1(@DomainFunctionParam("collection") Collection c, @DomainFunctionParam("a") Object... a) {
            return 1;
        }
        public static Integer size2(@DomainFunctionParam("collection") Collection<?> c, @DomainFunctionParam("a") Object... a) {
            return 2;
        }
        public static Integer size3(@DomainFunctionParam("collection") Collection<Integer> c, @DomainFunctionParam("a") Object... a) {
            return 3;
        }
        public static Collection sub1(Collection c) {
            return c;
        }
        public static Collection<?> sub2(Collection<?> c) {
            return c;
        }
        public static Collection<Integer> sub3(Collection<Integer> c) {
            return c;
        }
    }

    @Test
    public void testFunctions() {
        DomainBuilder builder = Domain.getDefaultProvider().createDefaultBuilder();
        builder.createBasicType("Integer", Integer.class).build();
        DeclarativeDomainConfiguration defaultConfiguration = DeclarativeDomain.getDefaultProvider().createDefaultConfiguration();
        defaultConfiguration.addDomainFunctions(TestFunctions.class);
        DomainModel domainModel = defaultConfiguration.createDomainModel(builder);
        Assert.assertEquals(6, domainModel.getFunctions().size());
        Assert.assertEquals(domainModel.getType("Integer"), ((CollectionDomainType) domainModel.getFunction("sub3").getResultType()).getElementType());
        Assert.assertEquals(domainModel.getType("Integer"), ((CollectionDomainType) domainModel.getFunction("sub3").getArgument(0).getType()).getElementType());
    }
}
