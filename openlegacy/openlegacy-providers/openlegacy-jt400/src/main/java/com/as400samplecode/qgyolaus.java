package com.as400samplecode;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

// Example program to call "Retrieve List of Authorized Users" (QGYOLAUS) API
public class qgyolaus {

	public static void main(String[] argv) {
		AS400 as400System; // com.ibm.as400.access.AS400
		ProgramCallDocument pcml; // com.ibm.as400.data.ProgramCallDocument
		boolean rc = false; // Return code from ProgramCallDocument.callProgram()
		String msgId, msgText; // Messages returned from the server
		Object value; // Return value from ProgramCallDocument.getValue()

		int[] indices = new int[1]; // Indices for access array value
		int nbrRcds, // Number of records returned from QGYOLAUS and QGYGTLE
		nbrUsers; // Total number of users retrieved
		String listStatus; // Status of list on the server
		byte[] requestHandle = new byte[4];

		System.setErr(System.out);

		// Construct AS400 without parameters, user will be prompted
		as400System = new AS400();

		try {
			// Uncomment the following to get debugging information
			// com.ibm.as400.data.PcmlMessageLog.setTraceEnabled(true);

			System.out.println("Beginning PCML Example..");
			System.out.println("    Constructing ProgramCallDocument for QGYOLAUS API...");

			// Construct ProgramCallDocument
			// First parameter is system to connect to
			// Second parameter is pcml resource name. In this example,
			// serialized PCML file "qgyolaus.pcml.ser" or
			// PCML source file "qgyolaus.pcml" must be found in the classpath.
			pcml = new ProgramCallDocument(as400System, "com.as400samplecode.qgyolaus");

			// All input parameters have default values specified in the PCML source.
			// Do not need to set them using Java code.

			// Request to call the API
			// User will be prompted to sign on to the system
			System.out.println("    Calling QGYOLAUS API requesting information for the sign-on user.");
			rc = pcml.callProgram("qgyolaus");

			// If return code is false, we received messages from the server
			if (rc == false) {
				// Retrieve list of server messages
				AS400Message[] msgs = pcml.getMessageList("qgyolaus");

				// Iterate through messages and write them to standard output
				for (AS400Message msg : msgs) {
					msgId = msg.getID();
					msgText = msg.getText();
					System.out.println("    " + msgId + " - " + msgText);
				}
				System.out.println("** Call to QGYOLAUS failed. See messages above **");
				System.exit(0);
			}
			// Return code was true, call to QGYOLAUS succeeded
			// Write some of the results to standard output
			else {
				boolean doneProcessingList = false;
				String programName = "qgyolaus";
				nbrUsers = 0;
				while (!doneProcessingList) {
					nbrRcds = pcml.getIntValue(programName + ".listInfo.rcdsReturned");
					requestHandle = (byte[])pcml.getValue(programName + ".listInfo.rqsHandle");

					// Iterate through list of users
					for (indices[0] = 0; indices[0] < nbrRcds; indices[0]++) {
						value = pcml.getValue(programName + ".receiver.name", indices);
						System.out.println("User:  " + value);

						value = pcml.getValue(programName + ".receiver.description", indices);
						System.out.println("\t\t" + value);
					}

					nbrUsers += nbrRcds;

					// See if we retrieved all the users.
					// If not, subsequent calls to "Get List Entries" (QGYGTLE)
					// would need to be made to retrieve the remaining users in the list.
					listStatus = (String)pcml.getValue(programName + ".listInfo.listStatus");
					if (listStatus.equals("2") // List is marked as "Complete"
							|| listStatus.equals("3")) // Or list is marked "Error building"
					{
						doneProcessingList = true;
					} else {
						programName = "qgygtle";

						// Set input parameters for QGYGTLE
						pcml.setValue("qgygtle.requestHandle", requestHandle);
						pcml.setIntValue("qgygtle.startingRcd", nbrUsers + 1);

						// Call "Get List Entries" (QGYGTLE) to get more users from list
						rc = pcml.callProgram("qgygtle");

						// If return code is false, we received messages from the server
						if (rc == false) {
							// Retrieve list of server messages
							AS400Message[] msgs = pcml.getMessageList("qgygtle");

							// Iterate through messages and write them to standard output
							for (AS400Message msg : msgs) {
								msgId = msg.getID();
								msgText = msg.getText();
								System.out.println("    " + msgId + " - " + msgText);
							}
							System.out.println("** Call to QGYGTLE failed. See messages above **");
							System.exit(0);
						}
						// Return code was true, call to QGYGTLE succeeded

					}
				}
				System.out.println("Number of users returned:  " + nbrUsers);

				// Call the "Close List" (QGYCLST) API
				pcml.setValue("qgyclst.requestHandle", requestHandle);
				rc = pcml.callProgram("qgyclst");
			}
		} catch (PcmlException e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
			System.out.println("*** Call to QGYOLAUS failed. ***");
			System.exit(0);
		}

		System.exit(0);
	}
}