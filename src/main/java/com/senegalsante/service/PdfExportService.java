package com.senegalsante.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.senegalsante.model.Medication;
import com.senegalsante.model.Profile;
import com.senegalsante.model.User;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] generateHealthRecord(User user) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Carnet de Santé Numérique - HEALTHSen", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // User Info
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("Informations du Compte", headerFont));
            document.add(new Paragraph("Email: " + user.getEmail()));
            document.add(new Paragraph("Téléphone: " + user.getPhoneNumber()));
            document.add(new Paragraph(" "));

            // Medicare / Medications
            document.add(new Paragraph("Médicaments Actuels", headerFont));
            List<Medication> meds = user.getMedications();
            if (meds.isEmpty()) {
                document.add(new Paragraph("Aucun médicament enregistré."));
            } else {
                com.lowagie.text.List list = new com.lowagie.text.List(true, 20);
                for (Medication med : meds) {
                    list.add(new ListItem(med.getName() + " - " + med.getDosage() + " (" + med.getFrequency() + ")"));
                }
                document.add(list);
            }
            document.add(new Paragraph(" "));

            // Profiles
            document.add(new Paragraph("Profils Famille", headerFont));
            for (Profile profile : user.getProfiles()) {
                document.add(new Paragraph("- " + profile.getFirstName() + " " + profile.getLastName() +
                        " (" + profile.getBirthDate() + ") - Groupe Sanguin: "
                        + (profile.getBloodGroup() != null ? profile.getBloodGroup() : "N/A")));
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
