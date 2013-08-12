package at.fhv.smartgrid.rasbpi.marketprice;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import at.fhv.marketprice.MarketPriceInformation;
import at.fhv.marketprice.MarketPriceList;
import at.fhv.smartgrid.rasbpi.internal.MarketPriceAtom;

public class MarketPriceHelper {

	public static String MARKETPRICE_XML_FILE_LOCATION = "/smartboiler/marketpriceinformation.xml";
	public static String MARKETPRICE_XSD_FILE = "MarketPriceInformationList.xsd";
	public static int QUARTER_OF_AN_HOUR = 60*15;
	
	private static File marketPriceInformationFile = new File(MARKETPRICE_XML_FILE_LOCATION);
	private static MarketPriceInformation mpi = null;
	private static long lastModifiedDate = 0;
	
	private static SchemaFactory schemaFactory = SchemaFactory
			.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	
	public static long getCurrentMarketPricesListTimestamp() {
		try {
			initOrCheckMarketPriceInformation();
		} catch (JAXBException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}


	public static List<MarketPriceAtom> getCurrentMarketPrices() {
		try {
			initOrCheckMarketPriceInformation();
			
			long referenceDate = mpi.getReferenceDateTime().toGregorianCalendar().getGregorianChange().getTime();
			List<at.fhv.marketprice.MarketPriceAtom> mpl = mpi.getPriceList().getPriceEntry();
			
			List result = new ArrayList<>(mpl.size());
			for (at.fhv.marketprice.MarketPriceAtom atom : mpl) {
				int ordinal = atom.getOrdinalNumberAndPrice().get(0).getValue();
				
				MarketPriceAtom mpa = new MarketPriceAtom();
				mpa.validFrom = referenceDate + (ordinal*QUARTER_OF_AN_HOUR);
				mpa.price = atom.getOrdinalNumberAndPrice().get(1).getValue();
				
				result.add(mpa);
			}
			
			return result;

		} catch (JAXBException | SAXException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private static void initOrCheckMarketPriceInformation() throws JAXBException, SAXException {
		if(marketPriceInformationFile.lastModified()>lastModifiedDate) {
			mpi = unmarshallFile(marketPriceInformationFile);
		}
		
	}
	
	public static MarketPriceInformation unmarshallFile(File xmlFile) throws JAXBException, SAXException{
		System.out.println("| Unmarshalling file "+xmlFile);
		Unmarshaller u = JAXBContext.newInstance(MarketPriceInformation.class).createUnmarshaller();
		URL schemaLocation = MarketPriceHelper.class.getResource(MARKETPRICE_XSD_FILE);
		Schema schema = schemaFactory.newSchema(schemaLocation);
		u.setSchema(schema);
		JAXBElement obj = (JAXBElement) u.unmarshal(xmlFile);
		return (MarketPriceInformation) obj.getValue();
	}
}
