package org.openlegacy.providers.jt400.mockup;

import org.openlegacy.annotations.rpc.RpcBooleanField;
import org.openlegacy.annotations.rpc.RpcDateField;
import org.openlegacy.annotations.rpc.RpcField;
import org.openlegacy.annotations.rpc.RpcPart;
import org.openlegacy.annotations.rpc.Scope;

import java.util.Date;

@RpcPart
public class UserInformation {

	@RpcField(length = 4, scope = Scope.PRIVATE)
	Integer bytesReturned;

	@RpcField(length = 4, scope = Scope.PRIVATE)
	Integer bytesAvailable;

	@RpcField(length = 10)
	String userProfile;

	@RpcDateField(pattern = "YYYMMDD")
	@RpcField(length = 7)
	Date previousSignonDate;

	@RpcDateField(pattern = "YYYMMDD")
	@RpcField(length = 6)
	Date previousSignonTime;

	@RpcField(length = 6)
	byte _;

	@RpcField(length = 4)
	int badSignonAttempts;

	@RpcField(length = 10)
	String status;

	@RpcField(length = 8)
	byte[] passwordChangeDate;

	@RpcBooleanField(falseValue = "N", trueValue = "Y")
	@RpcField(length = 8)
	Boolean noPassword;

	@RpcField(length = 8)
	int passwordExpirationInterval;

	Date datePasswordExpired;

	int daysUntilPasswordExpires;
}
