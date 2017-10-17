package nl.juraji.biliomi.model.internal.rest;

import nl.juraji.biliomi.utility.factories.ModelUtils;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 25-8-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "CommandRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogInfo {

  @XmlElement(name = "LogDate")
  private DateTime logDate;

  @XmlElement(name = "Lines")
  private List<String> lines;

  public DateTime getLogDate() {
    return logDate;
  }

  public void setLogDate(DateTime logDate) {
    this.logDate = logDate;
  }

  public List<String> getLines() {
    this.lines = ModelUtils.initCollection(this.lines);
    return lines;
  }
}
