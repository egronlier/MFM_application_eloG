package gscop.mfm_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tutoandroid.libmultispinner.MultiSelectionSpinner;

public class comments_item18 extends Activity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    String name = "";
    String surname = "";
    String birthdate = "";
    Button boutonEnregistrer;
    final Context context = this;
    RadioGroup radioGroupCotationPaper;
    TextView textCotationPaper;
    RadioButton boutonCotation0Paper;
    RadioButton boutonCotation1Paper;
    RadioButton boutonCotation2Paper;
    RadioButton boutonCotation3Paper;
    RadioButton boutonCotationNSPPaper;
    RadioGroup radioGroupCotationTablet;
    TextView textCotationTablet;
    RadioButton boutonCotation0Tablet;
    RadioButton boutonCotation1Tablet;
    RadioButton boutonCotation2Tablet;
    RadioButton boutonCotation3Tablet;
    RadioButton boutonCotationNSPTablet;
    MultiSelectionSpinner listeComment;
    String cotationPaper = "cotation papier inconnue";
    String cotationTablet = "cotation tablette inconnue";
    String commentaire = "aucun commentaire";
    EditText comments;
    TextView infosPatient;
    String path = "";
    ArrayList tableauX;
    ArrayList tableauY;
    Bitmap cartoBitmap;
    File myFile;
    List<String> listeComm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_item18);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // on récupère les infos de l'intent
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            birthdate = intent.getStringExtra("birthdate");
            path = intent.getStringExtra("path");
            tableauX = intent.getIntegerArrayListExtra("tableauX");
            tableauY = intent.getIntegerArrayListExtra("tableauY");
            try {
                File f = new File(path, "cartographie.png");
                cartoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.errorCarto, Toast.LENGTH_LONG).show();
            }
            // on remplit la liste déroulante
            String[] array = {"-", "difficulté", "sans appui de la main", "avec appui de la main", "arrêt", "change de doigt", "avec compensation"};
            listeComment = (MultiSelectionSpinner) findViewById(R.id.mySpinner);
            listeComment.setItems(array);
            listeComment.setListener(this);
        }

        radioGroupCotationTablet = (RadioGroup) findViewById(R.id.radioGroupCotationTablet);
        textCotationTablet = (TextView) findViewById(R.id.textCotationTablet);
        boutonCotation0Tablet = (RadioButton) findViewById(R.id.radioButton0Tablet);
        boutonCotation1Tablet = (RadioButton) findViewById(R.id.radioButton1Tablet);
        boutonCotation2Tablet = (RadioButton) findViewById(R.id.radioButton2Tablet);
        boutonCotation3Tablet = (RadioButton) findViewById(R.id.radioButton3Tablet);
        boutonCotationNSPTablet = (RadioButton) findViewById(R.id.radioButtonNSPTablet);

        radioGroupCotationPaper = (RadioGroup) findViewById(R.id.radioGroupCotationPaper);
        textCotationPaper = (TextView) findViewById(R.id.textCotationPaper);
        boutonCotation0Paper = (RadioButton) findViewById(R.id.radioButton0Paper);
        boutonCotation1Paper = (RadioButton) findViewById(R.id.radioButton1Paper);
        boutonCotation2Paper = (RadioButton) findViewById(R.id.radioButton2Paper);
        boutonCotation3Paper = (RadioButton) findViewById(R.id.radioButton3Paper);
        boutonCotationNSPPaper = (RadioButton) findViewById(R.id.radioButtonNSPPaper);

        comments = (EditText) findViewById(R.id.editTextComments);
        comments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        infosPatient = (TextView) findViewById(R.id.PatientName);
        infosPatient.setText("Patient : " + name + " " + surname + " \nNé(e) le : " + birthdate);

        boutonEnregistrer = (Button) findViewById(R.id.buttonSave);
        boutonEnregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on évite que la personne clique 2 fois sur le bouton en le rendant non cliquable
                boutonEnregistrer.setClickable(false);

                // on vérifie qu'au moins un radioButton a été sélectionné dans chaque radioGroup
                // radioGroup : cotation papier
                if (boutonCotation0Paper.isChecked() || boutonCotation1Paper.isChecked() || boutonCotation2Paper.isChecked() || boutonCotation3Paper.isChecked() || boutonCotationNSPPaper.isChecked()) {
                    textCotationPaper.setError(null);
                    // radioGroup : cotation tablette
                    if (boutonCotation0Tablet.isChecked() || boutonCotation1Tablet.isChecked() || boutonCotation2Tablet.isChecked() || boutonCotation3Tablet.isChecked() || boutonCotationNSPTablet.isChecked()) {
                        textCotationTablet.setError(null);
                        // --------------------- on récupère les commentaires du kiné -------------------
                        // ------- COTATION PAPIER
                        int radioButtonSelectedID = radioGroupCotationPaper.getCheckedRadioButtonId();
                        View radioButtonSelected = radioGroupCotationPaper.findViewById(radioButtonSelectedID);
                        int index = radioGroupCotationPaper.indexOfChild(radioButtonSelected);
                        RadioButton r = (RadioButton) radioGroupCotationPaper.getChildAt(index);
                        cotationPaper = r.getText().toString();
                        // ------- COTATION TABLETTE
                        radioButtonSelectedID = radioGroupCotationTablet.getCheckedRadioButtonId();
                        radioButtonSelected = radioGroupCotationTablet.findViewById(radioButtonSelectedID);
                        index = radioGroupCotationTablet.indexOfChild(radioButtonSelected);
                        r = (RadioButton) radioGroupCotationTablet.getChildAt(index);
                        cotationTablet = r.getText().toString();
                        // ------- COMMENTAIRES
                        listeComm = listeComment.getSelectedStrings();
                        commentaire = comments.getText().toString();
                        // ------------------------------------------------------------------------------

                        // ouvrir une boite de dialogue permettant de valider la création du pdf
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder
                                .setTitle("Confirmation de validation")
                                .setMessage("Etes-vous certain de vouloir créer un fichier pour ce patient ?")
                                .setCancelable(false)
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, on fait l'enregistrement
                                        Toast.makeText(getApplicationContext(), R.string.pdfsaving, Toast.LENGTH_LONG).show();
                                        dialog.cancel();
                                        try {
                                            // ----------- CREATION DU PDF -------------
                                            createPdf();
                                            Toast.makeText(getApplicationContext(), R.string.savedOK, Toast.LENGTH_LONG).show();
                                        } catch (FileNotFoundException | DocumentException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), R.string.pbPDF, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close the dialog box
                                        dialog.cancel();
                                        boutonEnregistrer.setClickable(true);
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    } else {
                        boutonEnregistrer.setClickable(true);
                        Toast.makeText(getApplicationContext(), R.string.errorCotationTablet, Toast.LENGTH_LONG).show();
                        textCotationTablet.setError("Choisir cotation !");
                        textCotationTablet.requestFocus();
                    }
                } else {
                    boutonEnregistrer.setClickable(true);
                    Toast.makeText(getApplicationContext(), R.string.errorCotationPaper, Toast.LENGTH_LONG).show();
                    textCotationPaper.setError("Choisir cotation !");
                    textCotationPaper.requestFocus();
                }
            }
        });
    }

    // quand on appuie sur la touche retour de la tablette
    private boolean back_answer = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back_answer = true;
            // on revient à l'écran d'affichage de cartographie de l'item 18
            Intent myIntent = new Intent(comments_item18.this, carto_item18.class);
            myIntent.putExtra("name", name);
            myIntent.putExtra("surname", surname);
            myIntent.putExtra("birthdate", birthdate);
            myIntent.putExtra("path", path);
            myIntent.putExtra("tableauX", tableauX);
            myIntent.putExtra("tableauY", tableauY);
            startActivity(myIntent);
            // on ferme l'activité en cours
            finish();
        }
        return back_answer;
    }

    @Override
    public void selectedIndices(List<Integer> indices) {
    }

    @Override
    public void selectedStrings(List<String> strings) {
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void createPdf() throws FileNotFoundException, DocumentException {
        // on crée un dossier NOM_prenom du patient s'il n'existe pas déjà
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , "patient_" + name + "_" + surname);
        boolean isDirectoryCreated = pdfFolder.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = pdfFolder.mkdir();
        }
        if (isDirectoryCreated) {
            Toast.makeText(getApplicationContext(), R.string.directoryExist, Toast.LENGTH_SHORT).show();
        }

        //Create time stamp
        String timeStamp = new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss", Locale.FRANCE).format(new Date());
        String timeStampSimple = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date());

        // on crée le nom du fichier pdf à enregistrer
        String filePath = pdfFolder.toString() + "/" + name + "_" + surname + "_" + timeStamp + "_" + "item18.pdf";
        myFile = new File(filePath);
        OutputStream output = new FileOutputStream(myFile);

        //Step 1 : on crée le document
        Document document = new Document(PageSize.LETTER);
        document.setMarginMirroring(true);
        document.setMarginMirroringTopBottom(true);

        //Step 2 : on instantie le PdfWriter
        PdfWriter.getInstance(document, output);

        //Step 3 : ouverture du document
        document.open();

        //Step 4 : Add content
        // choix des polices
        Font myFontTitre = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        // TITRE
        Paragraph paragraphTitre = new Paragraph();
        paragraphTitre.setAlignment(Element.ALIGN_CENTER);
        paragraphTitre.setFont(myFontTitre);
        paragraphTitre.add("Fiche récapitulative \n \n \n");
        document.add(paragraphTitre);

        // INFOS PATIENT
        Paragraph paragraphInfosTitre = new Paragraph();
        paragraphInfosTitre.setFont(myFontTitre);
        paragraphInfosTitre.add("\n\n INFORMATIONS PATIENT : \n");
        document.add(paragraphInfosTitre);

        String strText = " Patient : " + name + " " + surname +
                "\n Date de naissance : " + birthdate + "\n \n";
        Paragraph paragraphInfos = new Paragraph();
        paragraphInfos.add(strText);
        document.add(paragraphInfos);

        // INFOS ITEM
        Paragraph paragraphInfosItemTitre = new Paragraph();
        paragraphInfosItemTitre.setFont(myFontTitre);
        paragraphInfosItemTitre.add("\n ITEM 18 :");
        document.add(paragraphInfosItemTitre);

        strText = "réalisé le : " + timeStampSimple +
                "cotation sur papier : " + cotationPaper +
                "cotation sur tablette : " + cotationTablet +
                "\n \n";
        Paragraph paragraphInfosItem = new Paragraph();
        paragraphInfosItem.add(strText);
        document.add(paragraphInfosItem);

        // COMMENTAIRES KINE
        Paragraph paragraphCommKineTitre = new Paragraph();
        paragraphCommKineTitre.setFont(myFontTitre);
        paragraphCommKineTitre.add("\n COMMENTAIRES : \n");
        document.add(paragraphCommKineTitre);

        String maListe = "";
        for (String elem : listeComm) {
            maListe = maListe + elem + " , ";
        }
        strText = maListe + "\n" + commentaire + "\n \n";
        Paragraph paragraphCommKine = new Paragraph();
        paragraphCommKine.add(strText);
        document.add(paragraphCommKine);

        // CARTOGRAPHIE
        // on change de page
        document.newPage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cartoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image trueImage = null;
        try {
            trueImage = Image.getInstance(stream.toByteArray());
            // on redimensionne l'image pour qu'elle rentre dans la page
            float leftMargin = document.leftMargin();
            float rightMargin = document.rightMargin();
            float pageSize = document.getPageSize().getWidth();
            float usablePageSize = pageSize - (rightMargin + leftMargin);
            float imageWidth = trueImage.getPlainWidth();
            if (imageWidth > usablePageSize) {
                float reduceWidth = imageWidth - usablePageSize;
                float reducePercent = 100f - ((reduceWidth * 100f) / imageWidth);
                trueImage.scalePercent(reducePercent);
            }
            trueImage.setAlignment(Image.MIDDLE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Paragraph paragraphCarto = new Paragraph();
        paragraphCarto.add(trueImage);
        document.add(paragraphCarto);

        // TABLEAU DES COORDONNEES
        // on change de page
        document.newPage();
        // 2 colonnes, une pour chaque tableau
        PdfPTable table = new PdfPTable(2);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        // titres : colonne 1 = coordonnées en X , colonne 2 = coordonnées en Y
        table.addCell("Coordonnées en X");
        table.addCell("Coordonnées en Y");
        table.setHeaderRows(1);
        // on met les cellules titre en gris
        PdfPCell[] cells = table.getRow(0).getCells();
        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(BaseColor.GRAY);
        }
        // on parcourt les coordonnées en X et on les ajoute en colonne 1
        for (int i = 1; i <= tableauX.size() - 1; i++) {
            table.addCell(tableauX.get(i).toString());
            table.addCell(tableauY.get(i).toString());
        }
        // on ajoute le tableau au document
        document.add(table);

        //Step 5: Close the document
        document.close();

        promptForNextAction();
    }

    private void viewPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void promptForNextAction() {
        final String[] options = {getString(R.string.label_continue), getString(R.string.label_preview), getString(R.string.label_quit)};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("PDF enregistré, que voulez-vous faire ?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_continue))) {
                    // on renvoie alors vers l'interface de choix d'item
                    Intent myIntent = new Intent(comments_item18.this, choice_item.class);
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("surname", surname);
                    myIntent.putExtra("birthdate", birthdate);
                    startActivity(myIntent);
                    // on ferme l'activité en cours
                    finish();
                } else if (options[which].equals(getString(R.string.label_preview))) {
                    try {
                        // on renvoie alors vers l'interface de choix d'item
                        Intent myIntent = new Intent(comments_item18.this, choice_item.class);
                        myIntent.putExtra("name", name);
                        myIntent.putExtra("surname", surname);
                        myIntent.putExtra("birthdate", birthdate);
                        startActivity(myIntent);
                        // on ferme l'activité en cours
                        finish();
                        // on ouvre le pdf
                        viewPdf();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.viewPB, Toast.LENGTH_LONG).show();
                    }
                } else if (options[which].equals(getString(R.string.label_quit))) {
                    comments_item18.this.finish();
                    System.exit(0);
                }
            }
        });
        builder.show();
    }
}

