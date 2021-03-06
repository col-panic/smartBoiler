//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.18 at 08:32:22 AM CET 
//


package com.bosch.vpp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Request">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="asset" type="{}Asset"/>
 *         &lt;element name="context" type="{}Context"/>
 *         &lt;element name="method" type="{}Method"/>
 *         &lt;element name="action" type="{}Action" minOccurs="0"/>
 *         &lt;element name="ping" type="{}Ping" minOccurs="0"/>
 *         &lt;element name="state" type="{}State" minOccurs="0"/>
 *         &lt;element name="measurement" type="{}Measurement" minOccurs="0"/>
 *         &lt;element name="timeSeries" type="{}TimeSeries" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Request", propOrder = {
    "asset",
    "context",
    "method",
    "action",
    "ping",
    "state",
    "measurement",
    "timeSeries"
})
public class Request {

    @XmlElement(required = true)
    protected Asset asset;
    @XmlElement(required = true)
    protected Context context;
    @XmlElement(required = true)
    protected Method method;
    protected Action action;
    protected Ping ping;
    protected State state;
    protected Measurement measurement;
    protected TimeSeries timeSeries;

    /**
     * Gets the value of the asset property.
     * 
     * @return
     *     possible object is
     *     {@link Asset }
     *     
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the value of the asset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Asset }
     *     
     */
    public void setAsset(Asset value) {
        this.asset = value;
    }

    /**
     * Gets the value of the context property.
     * 
     * @return
     *     possible object is
     *     {@link Context }
     *     
     */
    public Context getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *     allowed object is
     *     {@link Context }
     *     
     */
    public void setContext(Context value) {
        this.context = value;
    }

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link Method }
     *     
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link Method }
     *     
     */
    public void setMethod(Method value) {
        this.method = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link Action }
     *     
     */
    public Action getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link Action }
     *     
     */
    public void setAction(Action value) {
        this.action = value;
    }

    /**
     * Gets the value of the ping property.
     * 
     * @return
     *     possible object is
     *     {@link Ping }
     *     
     */
    public Ping getPing() {
        return ping;
    }

    /**
     * Sets the value of the ping property.
     * 
     * @param value
     *     allowed object is
     *     {@link Ping }
     *     
     */
    public void setPing(Ping value) {
        this.ping = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link State }
     *     
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link State }
     *     
     */
    public void setState(State value) {
        this.state = value;
    }

    /**
     * Gets the value of the measurement property.
     * 
     * @return
     *     possible object is
     *     {@link Measurement }
     *     
     */
    public Measurement getMeasurement() {
        return measurement;
    }

    /**
     * Sets the value of the measurement property.
     * 
     * @param value
     *     allowed object is
     *     {@link Measurement }
     *     
     */
    public void setMeasurement(Measurement value) {
        this.measurement = value;
    }

    /**
     * Gets the value of the timeSeries property.
     * 
     * @return
     *     possible object is
     *     {@link TimeSeries }
     *     
     */
    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    /**
     * Sets the value of the timeSeries property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeSeries }
     *     
     */
    public void setTimeSeries(TimeSeries value) {
        this.timeSeries = value;
    }

}
