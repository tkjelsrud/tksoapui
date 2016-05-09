// Useful snippets
// Use of JDialog with HTML
// Cross test-case extraction of values
// Dynamic calling of service (teststep) to get information/results
//
import groovy.json.JsonSlurper;
import javax.swing.*;

def project = testRunner.testCase.testSuite.project;
def ts = testRunner.testCase.testSuite;
def slurper = new JsonSlurper();
def oList = [:];
def maxQuery = 20;

for(tcn in ts.testCases) {
	def tc = tcn.getValue();
	def o = tc.getPropertyValue("TEST-ID");
	if(o != null && o != "") {
		o = o.toInteger();
		oList.put(0, 0);
		for(i = 0; i < 10; i++) {
			o--;
			oList.put(o, 0);
		}
	}
}



def tc = testRunner.testCase;
def st = "_HENT";
oList = oList.sort{it.key.toInteger()};

def html = "<html><table><tr><th>ID</th><th>Date</th><th>Status</th></tr>";

for(o in oList) {
	//log.info(o.getKey());
	tc.setPropertyValue("TEST-ID", o.getKey().toString());
	
	def run = null;
	
	try {
		run = testRunner.runTestStepByName(st);
	}
	catch(Exception e) {log.warn(e.toString()); continue;}
		
	def json = "";
	def res = "";
		
	try {
		res = testRunner.testCase.getTestStepByName(st).testRequest.response.contentAsString;
		json = slurper.parseText(res);
	}
	catch(Exception e) {log.warn(e.toString());}

	if(json && json.id != null && json.id != "") {
		html += "<tr>${json.id}<td>${json.transaksjon.registrert}</td><td>${json.transaksjon.status}</td></tr>";
	}
	if(maxQuery-- == 0)
		continue;
}

html += "</table></html>";

JTextPane jt = new JTextPane();
jt.setContentType("text/html");
jt.setText(html);

JScrollPane js = new JScrollPane(jt);

JDialog w = new JDialog();
w.setSize(800, 400);
w.getContentPane().add(js);
w.setVisible(true);

return null;