package com.gcorso.myapplication.Explore;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gcorso.myapplication.Objects.Course;
import com.gcorso.myapplication.Objects.Lesson;
import com.gcorso.myapplication.Objects.Level;
import com.gcorso.myapplication.Objects.Section;

import java.util.ArrayList;
import java.util.List;


public class LessonsLDH {

    public static final String[] levels = {"Esordiente", "Principiante", "Esperto", "Maestro", "Hacker"};
    public static final int[] boundaries = {0, 100, 250, 450, 700, 1500};

    private static final int DATABASE_VERSION = 18;
    private static final String DATABASE_NAME = "lessons.db";
    private static final String TABLE_NAME_LESSONS = "lesson";
    private static final String TABLE_NAME_COURSES = "course";

    private static final String LESSON_COLUMN_ID = "_id";
    private static final String LESSON_COLUMN_TITLE = "title";
    private static final String LESSON_COLUMN_COURSEID = "courseid";
    private static final String[] LESSON_COLUMN_SECTIONS = {"section0", "section1", "section2", "section3", "section4", "section5", "section6", "section7", "section8", "section9"};
    private static final String LESSON_COLUMN_QUESTIONS = "questions";
    private static final String LESSON_COLUMN_RESULT = "result";
    private static final String LESSON_COLUMN_NSECTIONS = "nsections";

    private static final String COURSE_COLUMN_ID = "_id";
    private static final String COURSE_COLUMN_TITLE = "title";

    private LessonsDBOpenHelper openHelper;
    private SQLiteDatabase database;

    public LessonsLDH(Context context){
        openHelper = new LessonsDBOpenHelper(context);
        database = openHelper.getReadableDatabase();
    }

    public LessonsDBOpenHelper getOpenHelper (Context context){
        openHelper = new LessonsDBOpenHelper(context);
        return openHelper;
    }

    public SQLiteDatabase getWritableDatabase (Context context){
        openHelper = new LessonsDBOpenHelper(context);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    public List<Course> getCourses(){
        openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT " + COURSE_COLUMN_ID + ", " + COURSE_COLUMN_TITLE + " FROM " + TABLE_NAME_COURSES,
                null
        );

        List<Course> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Course course = new Course(cursor.getInt(0), cursor.getString(1));
            List<Lesson> lessons = new ArrayList<>();
            Cursor curles = database.rawQuery( "SELECT _id, title, result, nsections FROM lesson WHERE courseid = " + Integer.toString(course.getCourseid()), null);
            curles.moveToFirst();
            while (!curles.isAfterLast()){
                Lesson lesson = new Lesson(curles.getInt(0), curles.getString(1), curles.getInt(2),curles.getInt(3));
                lessons.add(lesson);
                curles.moveToNext();
            }
            if (curles != null && !curles.isClosed()) {
                curles.close();
            }
            course.setLessons(lessons);
            list.add(course);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public Section getSection(int lessonid, int sectionn){
        String sql = "SELECT title, nsections, " + LESSON_COLUMN_SECTIONS[sectionn] + " FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        String lessontitle = cursor.getString(0);
        int lessonsections = cursor.getInt(1);
        String sec = cursor.getString(2);
        String title = sec.split("<<-->>")[0];
        String text = sec.split("<<-->>")[1];
        cursor.close();

        return new Section(lessonid, sectionn, title, text, lessontitle, lessonsections);

    }

    public String getQuestion(int lessonid){
        String sql = "SELECT questions FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        String questions = cursor.getString(0);
        cursor.close();
        return questions;

    }

    public String getLessonTitle(int lessonid){
        String sql = "SELECT title FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        String title = cursor.getString(0);
        cursor.close();
        return title;

    }

    public Level getLevel(){
        String sql = "SELECT SUM(result) FROM lesson";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        int totq = cursor.getInt(0) * 5;
        cursor.close();
        int liv = 0;
        for(int i = 4; i>=0; i--){
            if(totq>=boundaries[i]){
                liv = i;
                break;
            }
        }

        int prog = totq-boundaries[liv];
        int tot = boundaries[liv+1] - boundaries[liv];
        int perc = prog*100/tot;
        int[] perccourses = {0,0,0,0,0,0};
        String sql2 = "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 1 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 2 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 3 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 4 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 5 UNION " +
                "SELECT SUM(result), COUNT(_id), courseid FROM lesson WHERE courseid = 6";
        cursor = database.rawQuery(sql2, null);
        cursor.moveToFirst();

        for(int i = 0; i<6; i++){
            //Log.i("query risultati", "risultati " + Integer.toString(i) + " : " + Integer.toString(cursor.getInt(0)) + " / " + cursor.getInt(1));
            perccourses[cursor.getInt(2)-1] = cursor.getInt(0) * 10 / cursor.getInt(1);
            cursor.moveToNext();
        }
        cursor.close();

        return new Level(perc, levels[liv], prog, tot, perccourses);
    }

    public int updateResult(int lessonid, int nuovo){
        String sql = "SELECT result FROM lesson WHERE _id = " + Integer.toString(lessonid);
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        int vecchio = cursor.getInt(0);
        cursor.close();

        if(nuovo > vecchio){
            String update = "UPDATE lesson SET result = " + Integer.toString(nuovo) + " WHERE _id = " + Integer.toString(lessonid);
            database.execSQL(update);
            return nuovo-vecchio;
        }
        return 0;
    }

    // returns a book item complete for the ReadingActivity
    /*public Book getBookTextById(int id){
        openHelper.getWritableDatabase();
        String sql = "SELECT " + BOOK_COLUMN_ID + ", " + BOOK_COLUMN_NAME + ", " + BOOK_COLUMN_AUTHORID + " , "
                + BOOK_COLUMN_AUTHORNAME + ", " + BOOK_COLUMN_YEAR + ", "
                + BOOK_COLUMN_FILE + ", " + BOOK_COLUMN_PROGRESS + ", "
                + BOOK_COLUMN_NOTES
                + " FROM " + TABLE_NAME_BOOKS
                + " WHERE " + BOOK_COLUMN_ID + " = " + Integer.toString(id);

        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        Book book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                cursor.getString(3), cursor.getInt(4), cursor.getString(5),
                cursor.getInt(6), cursor.getString(7));

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        Time time = new Time();
        time.setToNow();
        sql = "UPDATE " + TABLE_NAME_BOOKS
                + " SET " + BOOK_COLUMN_OPENED + " = " + Long.toString(time.toMillis(false))
                + " WHERE " + BOOK_COLUMN_ID + " = " + Integer.toString(id);
        database.execSQL(sql);

        return book;
    }*/


    // class that interfaces the connection between the LDH and the database
    private class LessonsDBOpenHelper extends SQLiteOpenHelper {

        public LessonsDBOpenHelper(Context context) {
          super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL_CREATE_TABLE_BOOK = "CREATE TABLE IF NOT EXISTS `lesson` (\n" +
                    "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t`courseid`\tINTEGER,\n" +
                    "\t`title`\tTEXT,\n" +
                    "\t`section0`\tTEXT,\n" +
                    "\t`section1`\tTEXT,\n" +
                    "\t`section2`\tTEXT,\n" +
                    "\t`section3`\tTEXT,\n" +
                    "\t`section4`\tTEXT,\n" +
                    "\t`section5`\tTEXT,\n" +
                    "\t`section6`\tTEXT,\n" +
                    "\t`section7`\tTEXT,\n" +
                    "\t`section8`\tTEXT,\n" +
                    "\t`section9`\tTEXT,\n" +
                    "\t`questions`\tINTEGER,\n" +
                    "\t`result`\tINTEGER DEFAULT 0,\n" +
                    "\t`nsections`\tINTEGER DEFAULT 0\n" +
                    ");\n" +
                    "INSERT INTO `lesson` VALUES (1,1,'Passwords','Introduzione<<-->>Affidiamo continuamente sempre più informazioni riservate agli account dei nostri social network, ritenendoli luoghi inviolabili grazie alla protezione offerta dalle password. Esse, però, sono solamente delle parole chiave e i malintenzionati, in un tempo abbastanza lungo, potrebbero giungere a scoprirle e rubare così i nostri dati. Molti, ancora, utilizzano come password il nome della propria fidanzata o quello del proprio animale domestico e, i più incauti, usano come password l’intramontabile sequenza “123456” (che è la password più utilizzata degli ultimi anni). Scoprire password di questo tipo è un gioco da ragazzi anche per un pirata informatico alle prime armi.\n" +
                    "Il primo passo per proteggere la tua privacy online è, quindi, la creazione di una password sicura, ovvero una password che non possa essere scoperta da un programma o una persona in un breve lasso di tempo. ','Linee guida da seguire<<-->>Per prima cosa, scegli una password abbastanza lunga (quindi composta da almeno 15-20 caratteri) alternando lettere, sia maiuscole che minuscole, numeri, segni di punteggiatura e simboli (es. !, ?, £, $, #, %, etc.). Utilizzando una sequenza di caratteri che non componga una parola o una frase di senso compiuto, infatti, scoraggerai i tentativi dei malintenzionati che effettuano attacchi basati sui dizionari. Tali attacchi si basano sulla ricerca della tua password in lunghissime liste di parole comune, come potrebbe essere un dizionario.\n" +
                    "Un altro accorgimento molto importante è quello di non utilizzare come password parole che possano in qualche modo essere indovinate da altre persone). Per andare un po’ più nello specifico, non utilizzare i nomi dei tuoi familiari, la tua data di nascita o dell’anniversario di matrimonio o altri dati facilmente riconducibili alla tua persona.\n" +
                    "È buona norma, inoltre, aggiornare costantemente le proprie password (possibilmente almeno 1 volta ogni 2-3 mesi), visto che nessuna password è sicura al 100%. Un altro aspetto da non trascurare è quello di utilizzare password diverse per ogni account: così facendo, anche se dovessero malauguratamente intercettare una delle tue chiavi d’accesso, la sicurezza degli altri account non verrebbe compromessa.','Creare una password sicura<<-->>Dopo aver visto alcuni consigli generali su come scegliere una password sufficientemente sicura, è giunto il momento di passare dalla teoria alla pratica. Esistono, infatti diversi strumenti utili per scegliere una password sicura. Si tratta fondamentalmente di generatori automatici di password che, rispettando le linee guida del capitolo precedente, creano delle chiavi d’accesso alquanto sicure.\n" +
                    "Uno dei più famosi generatori di password è Password Chart. Come indica il nome stesso di questo servizio (il termine inglese “chart” può essere reso in italiano “grafico” o “tabella“), permette di creare chiavi d’accesso utilizzando la tecnica della transcodifica. Come funziona questo servizio? In pratica l’utente digita una frase che permette di generare lo schema che verrà utilizzato per la creazione della password, poi digita la parola che verrà utilizzata come riferimento per la creazione della password e, a quel punto, il servizio assocerà a ciascuna lettera dell’alfabeto una specifica combinazione di numeri, lettere e simboli, realizzando così una password piuttosto sicura.','Password Manager<<-->>Password di questo tipo, però, possono risultare davvero molto difficili da ricordare e rischiano di essere dimenticate già dopo il primo utilizzo. È, quindi, necessario affidarsi a qualche strumento per aiutare la nostra memoria. Scartando i convenzionali post-it, che sono facili da smarrire e possono essere trovati da malintenzionati, puoi fare buon uso dei cosiddetti password manager, ovvero dei particolari programmi che, come una sorta di “cassaforte virtuale”, custodiscono le password dei propri account. Utilizzare i password manager permette di avere un duplice beneficio: in primis, le password vengono custodite al sicuro (e non è poco!) e, altro aspetto rilevante, non bisogna porsi il problema di ricordare tutte le proprie password perché è possibile aprire queste “cassaforti virtuali”, ricordando una sola password principale. Puoi utilizzare comodamente la funzione password manager che trovi nella sezione strumenti di questa app.',NULL,NULL,NULL,NULL,NULL,NULL,'La password più usata degli ultimi anni è:<->123456<->Abcdef<->Qwerty123<->password<<-->>Esistono password davvero sicure?<->No, qualunque password può essere trovata in un tempo abbastanza lungo<->Sì, la mia lo è<->No, perché tutte le password sono già state utilizzate<->Si, ma nessuno ne ha mai trovata una<<-->>Gli attacchi dizionario tentano di indovinare le nostre password:<->liste di parole di uso comune<->Usando dizionari di varie lingue diverse<->Usando parole messe in ordine alfabetico, come in un dizionario<->Con parole generate a caso da un computer<<-->>Password Chart ci permette di:<->Generare password davvero sicure<->Accedere alle nostre password senza utilizzarne nessuna<->Attaccare gli account dei nostri contatti email<->Monitorare la sicurezza della nostra password<<-->>Affidare le password a un password manager è molto comodo e veloce, ma non molto sicuro<->F<<-->>Utilizzare caratteri MAIUSCOLI e minuscoli nella nostra password aumenta la sua resistenza agli attacchi<->V<<-->>È conveniente usare i nomi dei nostri familiari nelle nostre password, perché sono facili da ricordare e, dopotutto, non facili da indovinare da utenti esteri<->F<<-->>Password Chart è un sistema inutile e scoraggiante, perché genera password incomprensibili<->F<<-->>Per accedere al nostro password manager è necessario ricordare un’unica, lunga master key<->V<<-->>È fortemente consigliato cambiare la propria password dopo qualche mese di utilizzo<->V',0,4);\n" +
                    "INSERT INTO `lesson` VALUES (2,1,'Antivirus',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (3,2,'Virus','Cosa sono e come si diffondono<<-->>Un virus informatico è simile ad un virus biologico: si tratta di un programma progettato e realizzato per causare danni all’interno del dispositivo infettato. I primi virus nascono insieme all’informatica moderna (1980) e nel corso dei decenni si sono evoluti per meglio adattarsi a un ambiente in continua trasformazione.  Da un punto di vista del loro ciclo di vita, dopo la fase \"riproduttiva\", i virus informatici iniziano a svolgere attività di varia natura: distruttive e/o di ostruzionismo. Come si diffondono? Tramite il trasferimento di file infetti da un computer ad un altro, e, cosa ancor più grave, possono attaccare computer collegati fra loro in rete.','Virus di prima generazione e virus recenti<<-->>Mentre i virus della prima generazione attaccavano soltanto i file eseguibili (che nel sistema operativo DOS sono riconoscibili in quanto hanno un estensione .COM o .EXE), i virus attuali sono in grado di inquinare molti altri tipi di file nonché di cambiare le istruzioni del BIOS caricate in RAM, di diffondersi attraverso gli stessi supporti fisici contenuti nel PC e di danneggiare persino l''hardware.\n" +
                    "Da non molto tempo sono infine comparsi dei nuovi tipi di virus, i cosiddetti Virus delle Macro che si appiccicano a file documento generati per esempio con Microsoft Word per Windows, Microsoft Excel ecc...','Come difendersi dai virus<<-->>1) Limitare lo scambio di file .EXE, .COM, .OVR, .OVL, .SYS, .DOC, .XLS e di sistema (.SYS) fra computer in rete ed evitare di usare i server di rete come ambienti di lavoro. \n" +
                    "2) Ridurre l''uso di programmi shareware e di pubblico dominio se non se ne conosce la provenienza. \n" +
                    "3) Non inserire il proprio \"disco sistema\" in un altro computer se non in condizione di \"protezione in scrittura\" e non aggiungere mai dati o file ai floppy contenenti programmi originali. \n" +
                    "4) Non eseguire mai programmi di origine sconosciuta. Se proprio lo si dovesse fare, avvalersi di un programma antivirus di \"SCANSIONE\" in modo di rilevare la eventuale presenza di virus. ',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'I virus informatici esistono da che si sono sviluppati i computer e l''informatica (primi anni ''80).<->V<<-->>I virus informatici  si diffondono tramite il trasferimento di files infetti da un computer ad un altro, ma non possono attaccare computers collegati fra loro in rete.<->F<<-->>I virus informatici, hanno sia poteri distruttivi che di ostruzionismo<->V<<-->>I virus attuali, al contrario di quelli di prima generazione,  attaccano solo i fili eseguibili.<->F<<-->>I virus attuali, al contrario di quelli di prima generazione, sono in grado di danneggiare anche l''hardware e la RAM.<->V<<-->>I virus della Macro sono un esempio di virus di prima generazione.<->F<<-->>I virus della macro sono molto pericolosi perché si possono facilmente trasmettere mediante lo scambio di files di tipo \"documento\" (es. posta elettronica).<->V<<-->>Tra le regole basilari di prevenzione dei virus informatici c''è la seguente: non utilizzare i file server di rete come stazioni di lavoro.<->V<<-->>Tra le regole di prevenzione c''è la seguente: utilizzare liberamente programmi shareware e di pubblico dominio.<->F<<-->>Tra le regole di prevenzione c''è la seguente: non eseguire mai programmi di origine sconosciuta. Se proprio lo si dovesse fare, avvalersi di un programma antivirus di \"SCANSIONE\" che esamini il contenuto del diskette in modo di rilevare la eventuale presenza di virus.<->V',0,3);\n" +
                    "INSERT INTO `lesson` VALUES (4,2,'Phishing',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (5,2,'Spam',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (6,3,'Cyberbullismo',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (7,3,'Facebook',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (8,3,'Instagram',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (9,4,'GDPR','Cos''è:<<-->>Il GDPR (General Data Protection Regulation) è il regolamento europeo sulla privacy operativo dal 25 maggio 2018. Esso prevede nuove regole per il trattamento di dati personali e stabilisce pesanti sanzioni per i trasgressori (privati o aziende). Essendo un regolamento (atto legislativo dell’Unione Europea insieme a direttive e decisioni), ha portata generale (in tutti i paesi dell’Unione) e applicabilità diretta.\n" +
                    "Si compone di 99 articoli, in cui, oltre a istituire la figura del “data protection officer”, responsabile dell’applicazione del Regolamento in ciascuna azienda, stabilisce nuove regole e nuovi diritti degli utenti.','A chi si applica:<<-->>L’impatto è più ampio di quanto si possa pensare: Il GDPR riguarda infatti tutte le aziende che gestiscono qualsiasi tipo di dato personale. Dalle informazioni sui propri dipendenti alla profilatura dei clienti per conto terzi, o ancora all’analisi di dati per attività di marketing.\n" +
                    "Le aziende che trasgrediscono tale regolamento possono incorrere in sanzioni fino a 20 milioni di euro, o, in caso di aziende più grandi, fino al 4% del fatturato.','Cosa cambia: nuove regole...<<-->>Secondo il nuovo regolamento, quando un''azienda cerca di accedere a dati personali, deve chiedere il consenso con «un linguaggio semplice e chiaro» (articolo 7), oltre a spiegare bene perché e a quale fine utilizzerà alcune informazioni (articolo 13). Deve anche dichiarare il «periodo di conservazione»: ovvero per quanto tempo si custodiranno i dati, fornendo all''utente un ulteriore elemento di valutazione per cedere o meno informazioni a proprio riguardo. Infine, in caso di violazione dei propri dati, il titolare è tenuto a notificare l’accaduto entro 72 ore.\n" +
                    "Gli utenti potranno accedere quindi a molte più informazioni sul trattamento dei propri dati, senza doverle cercare in kilometriche e complesse informative.','...e nuovi diritti<<-->>Tra i diritti per i cittadini introdotti dal GDPR vi sono anche: il diritto di accesso (articolo 15: il cittadino deve poter sapere subito come e perché stanno trattando i suoi dati), il diritto di rettifica (articolo 16: «la rettifica dei dati personali inesatti che lo riguardano»), diritto di oblio (articolo 17: l''utente può far cancellare i dati in suo proposito) e il diritto alla portabilità dei dati (articolo 20: l''utente può ricevere tutte le informazioni che lo riguardano e trasmetterle a un altro titolare). ',NULL,NULL,NULL,NULL,NULL,NULL,'GDPR sta per<->General Data Protection Regulation<->Gender Discrimination PRevention<->Generational Drugs Producers Reabilitation<->Great Dalmatas PRoject<<-->>E’ stato emanato da<->Unione Europea<->Parlamento italiano<->Garante della Privacy<->È presente nella Costituzione Italiana<<-->>Il GDPR riguarda<->Tutte le aziende che trattano dati personali<->Enti statali e istituzioni<->Social Networks<->Aziende con fatturato superiore ai 20 milioni di euro annui<<-->>In caso di trasgressione, le sanzioni<->Arrivano fino a 20 milioni di euro (o al 4% del fatturato)<->Sono a carico dello Stato<->Sono proporzionali al fatturato dell’azienda<->Sono sempre inferiori a 10 milioni di euro<<-->>Il GDPR istituisce la figura del “data protection officer”, responsabile dell’applicazione del Regolamento in ciascuna azienda<->V<<-->>L’azienda che utilizza i dati personali non ha l’obbligo di comunicare il cd. «periodo di conservazione» ovvero per quanto tempo si custodiranno i dati stessi<->F<<-->>Il diritto di accesso prevede che il cittadino deve poter sapere subito come e perché stanno trattando i suoi dati<->V<<-->>Il GDPR non prevede il diritto di rettifica – vale a dire la possibilità per il cittadino di chiedere la rettifica dei dati personali inesatti che lo riguardano<->F<<-->>Un altro diritto previsto dalla normativa è quello di oblio, in base al quale l''utente può far cancellare i dati in suo proposito<->V<<-->>Il GDPR non ha concesso il diritto alla portabilità dei dati, in base al quale l''utente potrebbe ricevere tutte le informazioni che lo riguardano e trasmetterle a un altro titolare<->F',0,4);\n" +
                    "INSERT INTO `lesson` VALUES (10,4,'Crittografia','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (11,5,'VPN','Cos''è e come funziona<<-->>La VPN, nata per le aziende ma ora usata anche dai privati per aumentare la sicurezza via Internet, crea un tunnel virtuale tra il nostro computer e un server sicuro di proprietà del fornitore del servizio VPN. Tutto il traffico passa in modo criptato dal computer al server, per poi uscire “normalmente” su Internet. In sintesi, questo permette di navigare in modo privato e sicuro anche nelle reti Wi-Fi pubbliche. Grazie alla VPN, la navigazione Internet risulterà  provenire da un anonimo data center invece che dall’indirizzo IP intestato al sig. ‘Mario Rossi’.','Vantaggi e svantaggi <<-->>Oltra alla maggiore sicurezza informatica e alla protezione dei propri dati personali, la VPN permette di eludere le barriere geografiche. Infatti, praticamente tutti i servizi VPN, forniscono server in svariati Paesi. Con la VPN si può vedere un video sul sito della BBC, scegliendo un server nel Regno Unito, senza che il contenuto sia bloccato. Lo stesso vale per un sito negli Stati Uniti, o in Francia, o in Germania.\n" +
                    "Uno svantaggio  del navigare in rete attraverso la VPN è la limitata banda a disposizione e una certa lentezza nel collegarsi alle pagine web., lentezza dovuta al fatto che l’attività di codifica e decodifica dei dati criptati richiede del tempo.','Fattori da considerare<<-->>1) Disponibilità di Paesi:  si consiglia di scegliere un servizio VPN con almeno 25 paesi, verificando che siano inclusi i paesi utilizzati più spesso: Stati Uniti, Regno Unito, Germania, Paesi Bassi.\n" +
                    "2) Numero di dispositivi collegabili: in base alle esigenze proprie e familiari il numero può cambiare, ma si consiglia almeno 5 dispositivi collegabili.\n" +
                    "3) Costo: confrontate diversi provider guardando anche il prezzo. Quasi tutti richiedono un pagamento mensile o annuale.','Come collegarsi alla VPN<<-->>Molti provider offrono una app per smartphone che consente un accesso facile dai dispositivi mobili. Qualora così non fosse, è comunque possibile configurare OpenVPN for Android in modo simile a quanto si fa con un PC.',NULL,NULL,NULL,NULL,NULL,NULL,'L''acronimo VPN sta per Very Powerful Network<->F<<-->>VPN nasce per le reti aziendali<->V<<-->>La VPN crea un tunnel virtuale tra il nostro computer e un server sicuro di proprietà del fornitore del servizio VPN<->V<<-->>Le informazioni escono criptate dal computer al server <->V<<-->>Usando VPN la navigazione risulterà generata da anonimo data center<->V<<-->>La protezione di VPN non funziona sulle reti pubbliche<->F<<-->>VPN non mitiga gli effetti della cd. ''sorveglianza di massa‘<->F<<-->>VPN permette di  ridurre gli effetti delle barriere geografiche<->V<<-->>Tra i fattori da considerare nella scelta del servizio VPN è il numero di dispositivi collegabili mentre non ha importanza il numero di paesi disponibili<->F<<-->>Per collegarsi non è sempre necessario utilizzare una app fornita dal provider<->V',0,4);\n" +
                    "INSERT INTO `lesson` VALUES (12,5,'HTTPS',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (13,5,'DarkWeb',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (14,5,'WiFi',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (15,5,'WiFi Pubblico',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (16,6,'SQL injection',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (17,6,'Buffer overflow',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (18,2,'Malware',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (19,1,'Ethical Hacking',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (20,4,'Data breach',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (21,3,'Snapchat',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (22,4,'Cookies',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (23,3,'Twitter',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (24,2,'Worm',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (25,2,'Trojan Horse',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (26,5,'Firewall',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (27,6,'Proxy server',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (28,6,'Vulnerabilities',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "INSERT INTO `lesson` VALUES (29,6,'Diritti d''accesso',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0);\n" +
                    "CREATE TABLE IF NOT EXISTS `course` (\n" +
                    "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t`title`\tTEXT\n" +
                    ");\n" +
                    "INSERT INTO `course` VALUES (1,'Concetti chiave');\n" +
                    "INSERT INTO `course` VALUES (2,'Attacchi Informatici');\n" +
                    "INSERT INTO `course` VALUES (3,'Social Networks');\n" +
                    "INSERT INTO `course` VALUES (4,'Privacy');\n" +
                    "INSERT INTO `course` VALUES (5,'Internet');\n" +
                    "INSERT INTO `course` VALUES (6,'Sviluppatori')";
            String commands[] = SQL_CREATE_TABLE_BOOK.split(";");

            for(String cmd : commands){
                db.execSQL(cmd);
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COURSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LESSONS);

            onCreate(db);
        }
    }
}
