import javax.swing.*;

def tc = testRunner.testCase;
def tsList = tc.getTestStepList();
def dtf = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm");
def report = "<table style=\"font-family:Courier New\">" +
			"<tr><td>Tidspunk</td><td>Steg</td><td>Resp. (ms)</td><td>Resultat</td></tr>";

for(ts in tsList) {
	def tsn = ts.getLabel();

	if(ts.disabled)
		continue;
	
	def tl = ts.class.toString().split("\\.");
     def type = tl.last();
     //log.info(type);
     
     if(type in ["RestTestRequestStep", "HttpTestRequestStep", "WsdlTestRequestStep"]) {
     	//ts.testRequest.response.timeTaken;
     	def stat = ts.getAssertionStatus().toString();
     	
     	report += "<tr><td>" + dtf.format(ts.testRequest.response.getTimestamp()) + "</td>" +
     	     "<td>" + tsn + "</td>" +
     		"<td>" + ts.testRequest.response.timeTaken.toString() + "</td>" +
     		"<td>" + stat + "</td></tr>";
     }
	else {
		//def stat = ts.status.toString();
     	//report += "<td>" + ts.getPropertyValue("result").toString() + "</td>";
	}
}

JTextPane jt = new JTextPane();
jt.setContentType("text/html");
jt.setText(report);

JScrollPane js = new JScrollPane(jt);

JDialog w = new JDialog();
w.setSize(600, 640);
w.getContentPane().add(js);
w.setVisible(true);
