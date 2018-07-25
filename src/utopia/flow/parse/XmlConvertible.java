package utopia.flow.parse;

/**
 * Classes extending this interface can be converted to xml elements
 * @author Mikko Hilpinen
 * @since 14.3.2018
 */
public interface XmlConvertible // extends JdomConvertible
{
	// ABSTRACT	-----------------------
	
	/**
	 * @return An xml representation of this object
	 */
	public XmlElement toXmlElement();
	
	
	// IMPLEMENTED METHODS	-----------
	
	/*
	@Override
	public default Element toJdomElement()
	{
		return toXmlElement().toJdomElement();
	}*/
	
	/*
	 * Converts this model to a jdom element, possibly encoding the contents
	 * @param encode Should the element contents be encoded
	 * @return A jdom element based on this xml convertible
	 */
	/*
	public default Element toJdomElement(boolean encode)
	{
		return toXmlElement().toJdomElement(encode);
	}*/
}
