package jackpot;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Jackpot {

    private int[] resultado = new int[3];
    private float saldo = 0;
    private float deposito = 1000;
    private final float VALOR_APUESTA = 0.50F;

    private final int CEREZA = 0;
    private final int CAMPANA = 1;
    private final int TREBOL = 2;
    private final int MONEDA = 3;
    private final int SIETE = 4;

    private ArrayList<HistorialJackpot> listaHistorial = new ArrayList();
    private HistorialJackpot historial;  
    
    public float jugar() {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            resultado[i] = random.nextInt(5);
        }
        saldo -= VALOR_APUESTA;
        float premio = getPremio();
        saldo += premio;

        Calendar ahora = Calendar.getInstance();
        historial = new HistorialJackpot(ahora, saldo, deposito, premio, resultado.clone());
        listaHistorial.add(historial);

        return premio;
    }

    public ImageIcon getImagen(int indice) {
        try {
            ImageIcon imagen = new ImageIcon(getClass().getResource("/imagenes/" + resultado[indice] + ".png"));
            return imagen;
        } catch (NullPointerException ex) {
            Logger.getLogger(Jackpot.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private int getPremio() {
        if (resultado[0] == CEREZA && resultado[1] == CEREZA) {
            return 5;
        } else if (resultado[0] == CEREZA) {
            return 1;
        } else if (resultado[0] == CAMPANA && resultado[1] == CAMPANA && resultado[2] == CAMPANA) {
            return 10;
        } else if (resultado[0] == CAMPANA && resultado[1] == CAMPANA && resultado[2] == SIETE) {
            return 10;
        } else if (resultado[0] == TREBOL && resultado[1] == TREBOL && resultado[2] == TREBOL) {
            return 15;
        } else if (resultado[0] == TREBOL && resultado[1] == TREBOL && resultado[2] == SIETE) {
            return 15;
        } else if (resultado[0] == MONEDA && resultado[1] == MONEDA && resultado[2] == MONEDA) {
            return 20;
        } else if (resultado[0] == MONEDA && resultado[1] == MONEDA && resultado[2] == SIETE) {
            return 20;
        } else if (resultado[0] == SIETE && resultado[1] == SIETE && resultado[2] == SIETE) {
            return 100;
        } else {
            return 0;
        }
    }

    public void introducirMoneda(float valor) {
        this.saldo += valor;
        deposito += valor;
    }

    public float getSaldo() {
        return saldo;
    }

    public float getDeposito() {
        return deposito;
    }

    public void cobrar() {
        deposito -= saldo;
        saldo = 0;
    }

    public void exportarHistorialXML() {
        try {
            DocumentBuilderFactory fábricaCreadorDocumento = DocumentBuilderFactory.newInstance();
            DocumentBuilder creadorDocumento = fábricaCreadorDocumento.newDocumentBuilder();
            Document documento = creadorDocumento.newDocument();

            Element raiz = documento.createElement("historial");
            documento.appendChild(raiz);

            for (HistorialJackpot h : listaHistorial) {
                Element jugada = documento.createElement("jugada");
                raiz.appendChild(jugada);

                Element e = documento.createElement("fecha_hora");
                jugada.appendChild(e);
                DateFormat formato = DateFormat.getDateTimeInstance();
                String strFechaHora = formato.format(h.getCalendar().getTime());
                e.appendChild(documento.createTextNode(strFechaHora));

                e = documento.createElement("saldo");
                jugada.appendChild(e);
                e.appendChild(documento.createTextNode(String.valueOf(h.getSaldo())));

                e = documento.createElement("deposito");
                jugada.appendChild(e);
                e.appendChild(documento.createTextNode(String.valueOf(h.getDeposito())));

                e = documento.createElement("premio");
                jugada.appendChild(e);
                e.appendChild(documento.createTextNode(String.valueOf(h.getPremio())));

                e = documento.createElement("combinacion");
                jugada.appendChild(e);
                e.appendChild(documento.createTextNode("" + h.getCombinacion()[0] + h.getCombinacion()[1] + h.getCombinacion()[2]));
            }

            TransformerFactory fábricaTransformador = TransformerFactory.newInstance();
            Transformer transformador = fábricaTransformador.newTransformer();
            transformador.setOutputProperty(OutputKeys.INDENT, "yes");
            transformador.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "3");
            Source origen = new DOMSource(documento);

            Result destino = new StreamResult("historial.xml");
            transformador.transform(origen, destino);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Jackpot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(Jackpot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Jackpot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ArrayList<HistorialJackpot> getListaHistorial() {
        return listaHistorial;
    }

    public Document HistorialXML (String xmlFileName) {
        return getHistorialXML(xmlFileName);
    }
    
    private Document getHistorialXML(String xmlFileName) {
        Document documentoHistorial = null;
        try {
            DocumentBuilderFactory fabricaCreadorDocumento = DocumentBuilderFactory.newInstance();
            DocumentBuilder creadorDocumento = fabricaCreadorDocumento.newDocumentBuilder();
            documentoHistorial = creadorDocumento.parse(xmlFileName);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(HistorialJackpot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(HistorialJackpot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HistorialJackpot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return documentoHistorial;
    }

    public ArrayList<int[]> getCombinacionesGanadoras(Document documentoHistorial) {
        NodeList combinaciones = documentoHistorial.getElementsByTagName("jugada");
        ArrayList<int[]> combinacionesGanadoras;
        combinacionesGanadoras = new ArrayList();

        for (int i = 0; i < combinaciones.getLength(); i++) {
            Node nodeCombinacion = combinaciones.item(i);
            NodeList hijos = nodeCombinacion.getChildNodes();
            String[] d = new String[5]; // 5 campos 
            int k = 0;
            for (int j = 0; j < hijos.getLength(); j++) {
                if (hijos.item(j).getNodeType() == ELEMENT_NODE) {
                    d[k++] = hijos.item(j).getTextContent();
                }
            }
            int[] numeros = toNumeros(d[4]);
            combinacionesGanadoras.add(numeros);
        }
        return combinacionesGanadoras;
    }
    
    private int[] toNumeros(String numeros) {
        int[] numbers = new int[3];
        int aux = Integer.valueOf(numeros), divisor = 100, i;
        for (i = 0; i < 2; i++) {
            numbers[i] = aux/divisor;
            aux -= numbers[i] * divisor;
            divisor /= 10;
        }
        numbers[i] = aux;
        return numbers;
    }
}
