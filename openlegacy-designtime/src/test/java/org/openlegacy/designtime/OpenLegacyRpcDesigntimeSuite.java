package org.openlegacy.designtime;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openlegacy.designtime.rpc.source.parsers.CobolNumberInformationTest;
import org.openlegacy.designtime.rpc.source.parsers.CobolParserTest;
import org.openlegacy.designtime.rpc.source.parsers.CobolParserUtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ CobolParserTest.class, CobolParserUtilsTest.class, CobolNumberInformationTest.class })
public class OpenLegacyRpcDesigntimeSuite {

}
