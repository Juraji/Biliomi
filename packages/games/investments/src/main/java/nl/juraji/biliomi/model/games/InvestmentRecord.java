package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * Created by Juraji on 27-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "InvestmentRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvestmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement(name = "Id")
    private long id;

    @ManyToOne
    @XmlElement(name = "Invester")
    private User invester;

    @Column
    @NotNull
    @XmlElement(name = "Invested")
    private long invested;

    @Column
    @NotNull
    @XmlElement(name = "Interest")
    private double interest;

    @Transient // Used during actual investment
    @XmlTransient
    private boolean marketStateGood;

    @Column
    @XmlElement(name = "Project")
    private String project;

    @Column
    @XmlElement(name = "Payout")
    private long payout;

    @Column
    @Type(type = DateTimeISO8601Type.TYPE)
    @XmlElement(name = "Date")
    private DateTime date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getInvester() {
        return invester;
    }

    public void setInvester(User invester) {
        this.invester = invester;
    }

    public long getInvested() {
        return invested;
    }

    public void setInvested(long invested) {
        this.invested = invested;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public boolean isMarketStateGood() {
        return marketStateGood;
    }

    public void setMarketStateGood(boolean marketStateGood) {
        this.marketStateGood = marketStateGood;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String goal) {
        this.project = goal;
    }

    public long getPayout() {
        return payout;
    }

    public void setPayout(long payout) {
        this.payout = payout;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
}
