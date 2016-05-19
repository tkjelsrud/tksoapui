package Report.tk;

class Report {

    String createReport(context, testRunner, fName, mlogPath) { 
        def groovyUtils = new com.eviware.soapui.support.GroovyUtils( context )
        def tcList = testRunner.testCase.testSuite.getTestCaseList();

        def fos = new FileOutputStream(fName, true);
        def pw = new PrintWriter(fos);

        def dtf = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm");
        def dtFrom = dtf.format(new Date());

        def printData = "";
        def errorData = "";
        def failures = 0;
        def notRun = 0;

        for(suite in testRunner.testCase.testSuite.project.getTestSuiteList()) {
          for(tc in suite.getTestCaseList()) {
            if(tc.disabled) {
                continue;
            }
            def tsList = tc.getTestStepList();
            def tcn = tc.getLabel();
            
            printData += tcn + "\n";
            for(ts in tsList) {
                def tsn = ts.getLabel();
                def doReport = true;
                
                if(doReport) {
                    try {
                        def tl = ts.class.toString().split("\\.");
                        def type = tl.last();
                        
                        if(type in ["HttpTestRequestStep", "WsdlTestRequestStep"] && !ts.disabled) {
                            def res = "";
                            def req = "";
                            def xmlReq = "";
                            def xmlRes = "";
                            def host = "-";
                            def stat = ts.getAssertionStatus().toString();
                            
                            if(ts.testRequest && ts.testRequest.response) {
                                res = ts.testRequest.response.getContentAsXml();
                                
                                req = ts.testRequest.response.getRequestContent();
                                
                                if(req != "")
                                    xmlReq = groovyUtils.getXmlHolder(req);
                                if(res != "")
                                    xmlRes = groovyUtils.getXmlHolder(res);
                            }
            
                            if(res != "" && req != "") {
                                def fnr = "-";
                                def rdt = dtf.format(ts.testRequest.response.getTimestamp());
                
                                try {
                                    fnr = xmlReq["//*:X/text()"];
                                }
                                catch(all) {}
                                        
                                if(res != null && res.length() > 0) {
                                    def rst = ts.testRequest.response.timeTaken;
                                    def mid = "";
                                    def cs = "";
                                    try {
                                        mid = xmlRes["//*:X/text()"];
                                        if(!mid.isNumber())
                                            mid = "";
                                    }
                                    catch(all) {}	
                                    
                                    if(mid) {
                                        def expectSt = "X";
                                        if(tsn.endsWith("]"))
                                            expectSt = tsn[-3..-2];
                                        
                                        testRunner.testCase.getTestStepByName(mstatus).getHttpRequest().setEndpoint(X + X);
                                        def run = testRunner.runTestStepByName(mstatus);
                                        cs = "";
                                        def csLast = "";
                                        try {
                                            
                                            def xml = groovyUtils.getXmlHolder(testRunner.testCase.getTestStepByName(mstatus).getAssertableContentAsXml());
                                            cs = xml["//X/X/id/text()"];
                                            csLast = xml["//X[last()]/X/id/text()"];
                                            //X
                                        }
                                        catch(all) {
                                            errorData += tsn + all;
                                        }

                                        if(csLast == expectSt)
                                            stat = "VALID:${csLast}";
                                        else if(csLast == "FM" || csLast == "FT")
                                            stat = "FAIL:${csLast}";
                                        
                                        //X
                                    }
                                    else {
                                        cs = "";
                                        //X
                                    }
                                    
                                    def fau = xmlRes["//*:faultstring/text()"];
                                    if(fau && showFail) {
                                        errorData += "X";
                                    }
                                }
                            }
                            else {
                                notRun += 1;
                            }
                        }
                    }
                    catch(all) {
                        errorData += all;// + "\n";
                    }
                }
            }
        }
        }

        pw.println("");
        pw.println("*** " + dtFrom + " REPORT [${fName}] ****");

        if(failures > 0) {
            pw.println("FEIL: ** Testen har ${failures} avvik");
        }
        if(notRun > 0) {
            pw.println("MERK: ** Testen har ${notRun} tester som ikke er kjørt");
        }

        pw.println("");
        pw.println("RUN DATE         \t RESULT\tX    \t X        \t TM/ms \t TEST \t STATUS");
        pw.println(printData);

        pw.close();
        fos.close();
        return [printData, errorData];
    }
}