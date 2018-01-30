
import javax.swing.*;
import javax.swing.text.*;



import java.awt.*;
import java.awt.event.*;
import java.lang.Object;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	/*Postavljanje komponeti framova i highlitera*/
	private JFrame frame;
	private Action boldAction, italicAction, copyAction, cutAction, pasteAction, findAction;
	private JTextArea mainTextArea, lbl1, lbl2, lbl3;
	private JTextField findField;
	private int boldCheck, italicsCheck, caseCheck, keepSearch;
	private JComboBox<String> fontTypeList, fontSizeList;
	private JButton okFind, find, findAll;
	private JDialog findDialog;
	private JCheckBox caseSensitive;
	private Highlighter highlighter, highlighter1, highlighter2, highlighter3;
	private long start, end;
	private JLabel lblregex,lblregex1,lblRabinKarp,lblRabinKarp1,lblBojerMur,lblBojerMur1;
	private double vremeregex,vremerabin,vremebojer;

	public static void main(String[] args) {
		Main main = new Main();
		main.frameRun();
	}
   /*Metoda na kojoj se postavljaju centralne konponente i setuju se Highliter-i za lbl(1,2,3) tj za text-aree za proveru
    * pretrage*/
	private void frameRun() {
		frame = new JFrame();
		JPanel background = new JPanel();
		JPanel tarea = new JPanel();
		JPanel jvreme = new JPanel();
		lblregex = new JLabel("vreme izvršenja regex-a je: ");
		lblregex1 = new JLabel();
		lblRabinKarp = new JLabel("vreme izvršenja Rabin Karp algoritma je: ");
		lblRabinKarp1 = new JLabel();
		lblBojerMur = new JLabel("vreme izvršenja Bojer-Mur algoritma: ");
		lblBojerMur1 = new JLabel();
		lbl1 = new JTextArea("lbl");
		lbl2 = new JTextArea("lbl");
		lbl3 = new JTextArea("lbl");
		tarea.add(new JLabel("Regex: "));
		tarea.add(lbl1);
		tarea.add(new JLabel("Rabin Karp: "));
		tarea.add(lbl2);
		tarea.add(new JLabel("Bojer-Mur: "));
		tarea.add(lbl3);
		jvreme.add(lblregex);
		jvreme.add(lblregex1);
		jvreme.add(lblRabinKarp);
		jvreme.add(lblRabinKarp1);
		jvreme.add(lblBojerMur);
		jvreme.add(lblBojerMur1);
		frame.getContentPane().add(BorderLayout.CENTER, background);
		frame.setTitle("Tex editor");
		frame.setLocationByPlatform(true);
		background.setLayout(new BorderLayout());
		createActions();

		highlighter3 = lbl1.getHighlighter();
		highlighter1 = lbl2.getHighlighter();
		highlighter2 = lbl3.getHighlighter();
		frame.setJMenuBar(getMenu());

		background.add(BorderLayout.CENTER, getTextArea());
		background.add(BorderLayout.PAGE_START, getToolBar());
		background.add(BorderLayout.EAST, tarea);
		background.add(BorderLayout.PAGE_END,jvreme);
		findDialog = getfind();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setVisible(true);

	}
	/*Metoda za kreiranje meniBar-a koja poziva JMenuBar i postavlja action-e file,edit.
	 * Unutar edit-a postavlja action-e copy, cut, paste i find */
	private JMenuBar getMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");

		menuBar.add(file);
		menuBar.add(edit);

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new exitListener());

		file.add(exit);

		JMenuItem find = new JMenuItem(findAction);
		JMenuItem copy = new JMenuItem(copyAction);
		JMenuItem cut = new JMenuItem(cutAction);
		JMenuItem paste = new JMenuItem(pasteAction);

		edit.add(copy);
		edit.add(cut);
		edit.add(paste);
		edit.add(find);

		return menuBar;
	}

	private JScrollPane getTextArea() {
		mainTextArea = new JTextArea();
		highlighter = mainTextArea.getHighlighter();
		Font mainFont = new Font("Ariala", Font.PLAIN, 45);

		mainTextArea.setFont(mainFont);
		mainTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		mainTextArea.addMouseListener(new ClickListener());
		mainTextArea.setLineWrap(true);
		mainTextArea.setWrapStyleWord(true);

		JScrollPane textScroll = new JScrollPane(mainTextArea);
		textScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		return textScroll;
	}

	private JToolBar getToolBar() {
		boldCheck = 0;
		italicsCheck = 0;
		JToolBar toolBar = new JToolBar();
		AbstractButton bold = new JButton(boldAction);
		AbstractButton italics = new JButton(italicAction);
		toolBar.addSeparator();
		toolBar.add(getFontSizeList());
		toolBar.add(getFontsList());
		toolBar.addSeparator();
		toolBar.add(bold);
		toolBar.add(italics);
		toolBar.setFloatable(false);

		return toolBar;
	}

	private JComboBox<String> getFontsList() {
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fontTypeList = new JComboBox<String>(fonts);
		fontTypeList.addActionListener(new ComboBoxListener());
		fontTypeList.setSelectedIndex(6);
		fontTypeList.setPreferredSize(new Dimension(280, 28));
		fontTypeList.setMaximumSize(new Dimension(280, 28));
		return fontTypeList;
	}

	private JComboBox<String> getFontSizeList() {
		String[] size = new String[95];
		for (int i = 0; i < 95; i++) {
			size[i] = String.valueOf(i + 6);
		}
		fontSizeList = new JComboBox<String>(size);
		fontSizeList.setSelectedIndex(39);
		fontSizeList.setPreferredSize(new Dimension(49, 28));
		fontSizeList.setMaximumSize(new Dimension(49, 28));
		fontSizeList.addActionListener(new ComboBoxListener());

		return fontSizeList;
	}

	public JDialog getfind() {
		JDialog findWin = new JDialog(frame, "Find");
		JPanel findPanel = new JPanel();
		findPanel.setLayout(new GridLayout(7, 1, 2, 2));
		JPanel findPanel1 = new JPanel(new GridLayout(1, 2));
		JPanel findPanel2 = new JPanel(new GridLayout(1, 2));
		JPanel findPanel3 = new JPanel(new GridLayout(1, 1));

		keepSearch = 0;
		findField = new JTextField();
		find = new JButton("Find");
		findAll = new JButton("Find All");
		find.addActionListener(new ButtonAndDialogListeners());
		findAll.addActionListener(new ButtonAndDialogListeners());
		caseSensitive = new JCheckBox("Case sensitive");
		caseSensitive.setSelected(true);
		caseSensitive.addActionListener(new ButtonAndDialogListeners());
		okFind = new JButton("OK");
		okFind.addActionListener(new ButtonAndDialogListeners());
		findPanel.add(findField);
		findPanel1.add(find);
		findPanel2.add(findAll);
		findPanel.add(findPanel1);
		findPanel.add(findPanel2);
		findPanel.add(findPanel3);
		findPanel.add(caseSensitive);
		findPanel.add(okFind);
		findWin.add(findPanel);
		findWin.pack();
		findWin.setLocationRelativeTo(frame);
		findWin.setVisible(false);
		findWin.setEnabled(false);
		return findWin;
	}
	/*Metoda createAction sluzi za kreiranje akcija i postavljanje dešavanja tj vrednosti.
	 * ako je u pitanju BoldAction pprimenice se Bold fon a precica koja se moze koristiti je Crtl+B.
	 * Na isti nacin se kreiraju i ostale precice*/
	private void createActions() {

		boldAction = new MainActions("Bold", null, "Bold font",
				KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
		italicAction = new MainActions("Italics", null, "Italic font",
				KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		copyAction = new MainActions("Copy", null, "Copy the selected text",
				KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		cutAction = new MainActions("Cut", null, "Copy the selected text",
				KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		pasteAction = new MainActions("Paste", null, "Copy the selected text",
				KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		findAction = new MainActions("Find", null, "Search for  input word",
				KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
	}

	/* Rabin Karp  algoritam prima pat tj string koji ima vrednost teksta za pretragu,
	 * strig txt tj vrednost originalnog teksta u kome se vrsi pretraga kao i q integer vrednost je pocetna vrednost za hash*/
	private void search(String pat, String txt, int q) {
		long start = System.currentTimeMillis();// usima trenutno vreme pocetka algoritma
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);//highliter se koristi za postavljanje boje
		int d = 256;//broj karaktera u alfabetu
		int M = pat.length();//Uzima duzinu teksta unesenog za pretragu
		int N = txt.length();//Uzima duzinu teksta koji se pretrazuje
		int i, j;//brojaci
		int p = 0; // sluzi za postavljanje hash vrednosti za trazeni tekst
		int t = 0; // sluzi za postavljanje hash vrednosti koja se pretrazuje
		int h = 1;//hash vrednost

		// Vrednost za h je (d,M-1)%q
		for (i = 0; i < M - 1; i++)
			h = (h * d) % q;

		//Racunanje hash vrednosti u prvom prikazu
		for (i = 0; i < M; i++) {
			p = (d * p + pat.charAt(i)) % q;
			t = (d * t + txt.charAt(i)) % q;
		}

		// Prolazi kroz tekst redom za vrednost N-M
		for (i = 0; i <= N - M; i++) {

			//U koliko se vrednosti hash-a trazeog i pretrazivanog teksta poklapaju pronasli smo 
			//i pretrazujemo ostale karaktere  
			if (p == t) {
				/* Prolazak za duzinu trazenog teksta */
				for (j = 0; j < M; j++) {
					if (txt.charAt(i + j) != pat.charAt(j))
						break;
				}

				//Ako je p= t i j =M tj velicina trazenog teksta ronasli smo vrednost koju trazimo
				if (j == M)
					try {
						highlighter1.addHighlight(i, i + pat.length(), painter);// vrsimo bojenje teksta od i pocetak i na kraju teksta koji smo pronasli
					} catch (BadLocationException ex) {
						
					}
				
				 JOptionPane.showMessageDialog(null, "Vrednost je pronađena na indeksu: "
				 + i);
			}

			// Racunamo za sledecu vrednost teksta
			// U koliko je vodeca cifra dodajemo poslednju cifru
			if (i < N - M) {
				t = (d * (t - txt.charAt(i) * h) + txt.charAt(i + M)) % q;

				//Ako dobije negativno t pretvaramo ga u pozitivno
				if (t < 0)
					t = (t + q);
			}
		}
		long end = System.currentTimeMillis();//Racunanje vremena kraja algoritma
		vremebojer =(end - start) / 1000.0;//racunanje vremena za izvrsenje Rabin-Karp algoritma
		lblRabinKarp1.setText(vremebojer+"");//Postavljanje na  labelu
	}

	/* Bojer-Mur  algoritam pretrage unose se vrednosti za pretragu tj tekst koji se trazi i tekst koji se pretrazuje*/
	private void searchBojerMur(String pat, String txt) {
		long start = System.currentTimeMillis();// usima trenutno vreme pocetka algoritma
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);//hightiliter za plavo
		Map<Character, Integer> mt = new HashMap<Character, Integer>();//Maka karaktera i integera
		// Losa vrednost preskovi i postavljanje vrednosti mape
		for (int i = 0; i < pat.length(); i++) {
			mt.put(pat.charAt(i), i);
		}
		int skip = 0;// intege za preskakanje
		int N = txt.length();//Duzina Teksta za pretragu
		int M = pat.length();//Suzina teksta koji se pretrazuje
		for (int i = 0; i <= N - M; i = i + skip) {//For petlja  na kojoj se na i dodaje vrednost za preskakanje
			skip = 0;
			for (int j = M - 1; j >= 0; j--) {//Prolazak kroz duzinu teksta za pretragu
				if (pat.charAt(j) != txt.charAt(i + j)) {//Ako se ne poklapaju vrednosti i vrednost
					if (mt.get(txt.charAt(i + j)) != null)//mape nije jednaka za karakter po indeksu nije jednaka null
						skip = Math.max(1, j - mt.get(txt.charAt(i + j)));//Racuna se nova vredost preskakanja
					else
						skip = j + 1;//U suprotnom vrednost mu je vredno j brojaca + 1
					break;
				}
			}
			if (skip == 0) {//Ako nam je vrednost skip-a 0 tj vrednost za preskakanje karaktera
				// Pronasli smo trazeni karakter
				try {
					highlighter2.addHighlight(i, i + pat.length(), painter);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}

				 JOptionPane.showMessageDialog(null, "Pronađena vrednost na indeksu: " +
				i);
				skip = 1; //prelazimo na sledeci karakter
			}
		}
		long end = System.currentTimeMillis();//Racunanje vremena kraja algoritma
		vremerabin =(end - start) / 1000.0;//racunanje vremena za izvrsenje Bojer-Mur algoritma
		lblBojerMur1.setText(vremerabin+"");//Postavljanje na  labelu
	}
	/*Metoda za pronalazenje uz pomoc regex-a*/
	private void findRegex(String pat, String text) {
		start = System.currentTimeMillis();// usima trenutno vreme pocetka algoritma
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);//Boji u zuto
		Pattern p = Pattern.compile("\\b" + pat + "\\b", Pattern.CASE_INSENSITIVE);// Proverava vrednost paterna u Case insensitive
		Matcher m = p.matcher(text);//koristi mechar za proeru
		while (m.find()) {//dok je find true
			try {
				highlighter3.addHighlight(m.start(), m.end(), painter);//boji pocetak i kraj
			} catch (BadLocationException ex) {
				
			}
		}
		end = System.currentTimeMillis();//Racunanje vremena kraja algoritma
		 vremeregex = (end - start) / 1000.0;//racunanje vremena za Regex-a algoritma
		 lblregex1.setText(vremeregex+"");//Postavljanje na  labelu
	}
	/*Klasican search*/
	public void find() {
		String text;
		int endPoint;
		int findPoint;

		text = mainTextArea.getText();

		String searchString = findField.getText();
		if (caseCheck == 1) {
			text = text.toLowerCase();
			searchString = searchString.toLowerCase();
		}

		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

		findPoint = text.indexOf(searchString, keepSearch);
		endPoint = findPoint + searchString.length();
		if (findPoint != -1) {
			try {
				highlighter.addHighlight(findPoint, endPoint, painter);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
			keepSearch = endPoint;
		} else {
			JOptionPane.showMessageDialog(findDialog, "\"" + findField.getText() + "\"" + " Nije pronadjena.");
		}
	}

	private class MainActions extends AbstractAction {

		private static final long serialVersionUID = 1L;
		/*Metoda za postavljanje ikonica */
		public MainActions(String name, ImageIcon icon, String desc, KeyStroke key) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(ACCELERATOR_KEY, key);
		}
		/*Desavanja klikom na dugmice u osnovnom panelu*/
		public void actionPerformed(ActionEvent event) {
			Object source = event.getActionCommand();

			if (source.equals("Bold")) {
				if (boldCheck == 0) {
					mainTextArea.setFont(
							new Font(mainTextArea.getFont().getName(), Font.BOLD, mainTextArea.getFont().getSize()));
					boldCheck = 1;
					italicsCheck = 0;
				} else if (boldCheck == 1) {
					mainTextArea.setFont(
							new Font(mainTextArea.getFont().getName(), Font.PLAIN, mainTextArea.getFont().getSize()));
					boldCheck = 0;
					italicsCheck = 0;
				}
			} else if (source.equals("Italics")) {
				if (italicsCheck == 0) {
					mainTextArea.setFont(
							new Font(mainTextArea.getFont().getName(), Font.ITALIC, mainTextArea.getFont().getSize()));
					italicsCheck = 1;
					boldCheck = 0;
				} else if (italicsCheck == 1) {
					mainTextArea.setFont(
							new Font(mainTextArea.getFont().getName(), Font.PLAIN, mainTextArea.getFont().getSize()));
					italicsCheck = 0;
					boldCheck = 0;
				}
			} else if (source.equals("Copy")) {
				mainTextArea.copy();
			} else if (source.equals("Cut")) {
				mainTextArea.cut();
			} else if (source.equals("Paste")) {
				mainTextArea.paste();
			} else if (source.equals("Find")) {
				if (mainTextArea.getSelectedText() != null) {
					findField.setText(mainTextArea.getSelectedText());

				}
				lbl1.setText(mainTextArea.getText());
				lbl2.setText(mainTextArea.getText());
				lbl3.setText(mainTextArea.getText());
				lbl1.setEditable(false);
				lbl2.setEditable(false);
				lbl3.setEditable(false);
				mainTextArea.setEditable(false);
				findDialog.setVisible(true);
				findDialog.setEnabled(true);
			}
		}
	}

	/*Listener za ComboBox*/
	private class ComboBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source == fontTypeList) {
				mainTextArea.setFont(new Font(fontTypeList.getSelectedItem().toString(),
						mainTextArea.getFont().getStyle(), mainTextArea.getFont().getSize()));

			} else if (source == fontSizeList) {
				mainTextArea.setFont(new Font(mainTextArea.getFont().getName(), mainTextArea.getFont().getStyle(),
						Integer.parseInt(fontSizeList.getSelectedItem().toString())));

			}
		}
	}

	private class ClickListener extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			getPopupMenu(e);
		}

		private void getPopupMenu(MouseEvent e) {
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem copy = new JMenuItem(copyAction);
			popupMenu.add(copy);
			JMenuItem cut = new JMenuItem(cutAction);
			popupMenu.add(cut);
			JMenuItem paste = new JMenuItem(pasteAction);
			popupMenu.add(paste);
			JMenuItem find = new JMenuItem(findAction);
			popupMenu.add(find);

			if (e.isPopupTrigger()) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	//Listener za exit
	private class exitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	//Listener za dugmice i sklanjanje boja
	private class ButtonAndDialogListeners implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == find) {
				highlighter.removeAllHighlights();
				keepSearch = 0;
				find();
			} else if (source == findAll) {
				highlighter1.removeAllHighlights();
				search(findField.getText(), lbl3.getText(), 101);
				highlighter3.removeAllHighlights();
				findRegex(findField.getText(), lbl1.getText());
				highlighter2.removeAllHighlights();
				searchBojerMur(findField.getText(), lbl2.getText());
			} else if (source == caseSensitive) {
				if (caseSensitive.isSelected()) {
					caseCheck = 0;
				} else {
					caseCheck = 1;
				}
			} else if (source == okFind) {
				findDialog.setVisible(false);
				findField.setText("find");
				mainTextArea.setEditable(true);
				mainTextArea.setCaretPosition(mainTextArea.getText().length());
			}

		}
	}
/*Tekst za pretragu.*/
	/*Slika 4.6 Izračunavanje Boyer
-Moore tabele pr
eskakanja
Rešenje
je
sledeće
:
Preračunati
indeks
najdesnijeg
pojavljivanja
karaktera
c
u
šablonu.(-1
ukoliko karakter nije u šablonu), Slik
a*/
}
