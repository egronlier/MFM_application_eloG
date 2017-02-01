package gscop.mfm_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class ouverture_appli extends Activity {

    Button boutonValider;
    Button boutonQuitter;
    Button boutonEffacer;
    EditText nomEntre;
    EditText prenomEntre;
    DatePicker datePicker;
    java.util.Date birthdate = null;
    RadioButton boutonDroitier;
    RadioButton boutonGaucher;
    String varDG;
    final Context context = this;
    Calendar dateTodayCal = Calendar.getInstance();
    Date dateTodayDa ;

    @Override
    /*
     méthode appelée à l'initialisation
    */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ouverture_appli);

        // on utilise la méthode findViewById pour récupérer le bouton quand on clique dessus
        // R est la classe qui contient les ressources
        boutonValider = (Button) findViewById(R.id.boutonvalider);
        boutonQuitter = (Button) findViewById(R.id.boutonquitter);
        boutonEffacer = (Button) findViewById(R.id.buttonerase);
        nomEntre = (EditText) findViewById(R.id.nom);
        prenomEntre = (EditText) findViewById(R.id.prenom);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        boutonDroitier = (RadioButton) findViewById(R.id.boutonDroitier);
        boutonGaucher = (RadioButton) findViewById(R.id.boutonGaucher);

        int yearToday = dateTodayCal.get(Calendar.YEAR);
        int monthToday = dateTodayCal.get(Calendar.MONTH);
        int dayToday = dateTodayCal.get(Calendar.DAY_OF_MONTH);
        dateTodayDa = dateTodayCal.getTime();
        // attention les mois commencent à 0
        dateTodayCal.set(yearToday,monthToday+1,dayToday);
        datePicker.init(yearToday, monthToday, dayToday, new DatePicker.OnDateChangedListener(){
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                birthdate = getDateFromDatePicker(datePicker);
            }
        });

        // on met un listener qui regarde quand on clique sur le bouton
        boutonValider.setOnClickListener(validerListener);
        boutonQuitter.setOnClickListener(quitterListener);
        boutonEffacer.setOnClickListener(effacerListener);
    }

    // Pour le bouton valider
    private View.OnClickListener validerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // On récupère le nom, le prénom et la date de naissance
            final String name = nomEntre.getText().toString();
            int length_name = name.length();
            final String surname = prenomEntre.getText().toString();
            int length_surname = surname.length();

            // On vérifie que tous les champs ont été remplis
            // on vérifie qu'au moins un radioButton a été sélectionné
            if (boutonDroitier.isChecked() || boutonGaucher.isChecked()) {
                // on vérifie que le nom et le prénom ont été sélectionnés
                if (length_name > 0 && length_surname > 0) {
                    // On vérifie que le nom et le prénom entrés contiennent bien que des lettres
                    if (Pattern.matches("[a-zA-ZáàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ-]*", name)
                            && Pattern.matches("[a-zA-ZáàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ-]*", surname)) {
                        // on vérifie qu'une date a bien été sélectionnée
                        if(birthdate != null) {
                            try {
                                // on vérifie que la date choisie est antérieure à la date du jour
                                if (birthdate.before(dateTodayDa)) {
                                    if (boutonDroitier.isChecked())
                                        varDG = "Droitier";
                                    else varDG = "Gaucher";
                                    // ouvrir une boite de dialogue permettant de valider les infos entrées
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                    // set titre
                                    alertDialogBuilder.setTitle("Confirmation des données");
                                    // on met la date choisie au bon format : DD/MM/AAAA
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    final String birthdateFormated = sdf.format(birthdate);
                                            // set dialog message
                                    alertDialogBuilder
                                            .setMessage("Etes-vous certain de vouloir créer un fichier pour le patient suivant : \n\n"
                                                    + name.toUpperCase() + " " + surname.toLowerCase() + "\n né(e) le : " + birthdateFormated + "\n " + varDG)
                                            .setCancelable(false)
                                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // if this button is clicked, go to next activity
                                                    dialog.cancel();
                                                    // On lance une nouvelle activité : l'interface du choix d'item
                                                    Intent myIntent = new Intent(ouverture_appli.this, choix_item.class);
                                                    myIntent.putExtra("name", name);
                                                    myIntent.putExtra("surname", surname);
                                                    myIntent.putExtra("birthdate", birthdateFormated);
                                                    startActivity(myIntent);
                                                }
                                            })
                                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // if this button is clicked, close the dialog box
                                                    dialog.cancel();
                                                }
                                            });
                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    // show it
                                    alertDialog.show();
                                } else { // Date entrée non antérieure à la date du jour
                                    Toast.makeText(getApplicationContext(),R.string.errorDateAfter,Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) { // Problème inconnu avec la date choisie
                                Toast.makeText(getApplicationContext(),R.string.internalError,Toast.LENGTH_LONG).show();
                            }
                        }else{ // aucune date n'a été choisie
                            Toast.makeText(getApplicationContext(),R.string.errorDate,Toast.LENGTH_LONG).show();
                        }
                    } else { // Un des champs de nom ou prénom n'est pas au bon format
                        Toast.makeText(getApplicationContext(),R.string.errorNames,Toast.LENGTH_LONG).show();
                    }
                } else { // Un des champs de nom ou prénom n'est pas rempli
                    Toast.makeText(getApplicationContext(),R.string.errorVoid,Toast.LENGTH_LONG).show();
                }
            } else { // Droitier ou gaucher n'a pas été choisi
                Toast.makeText(getApplicationContext(),R.string.errorRadioButton,Toast.LENGTH_LONG).show();
            }
        }
    };

    // Pour le bouton quitter
    private View.OnClickListener quitterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // On quitte l'application
            ouverture_appli.this.finish();
            System.exit(0);
        }
    };

    // Listener du bouton effacer
    private View.OnClickListener effacerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nomEntre.getText().clear();
            prenomEntre.getText().clear();
            datePicker.setSelected(false);
            boutonDroitier.setChecked(false);
            boutonGaucher.setChecked(false);
        }
    };

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }
}