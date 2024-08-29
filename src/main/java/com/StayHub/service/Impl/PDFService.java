package com.StayHub.service.Impl;

import com.StayHub.payload.BookingDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

@Service
public class PDFService {

    private static final String PDF_DIRECTORY ="D://SK//";
    public boolean generatePdf(String fileName, BookingDto bookingDto){
       try {
           Document document = new Document();
           PdfWriter.getInstance(document, new FileOutputStream(fileName));

           document.open();
           Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
           Chunk bookingConfirmation = new Chunk("Booking Confirmation", font);
           Chunk firstName = new Chunk("Guest firstName: "+bookingDto.getFirstName(), font);
           Chunk lastName = new Chunk("Guest lastName: "+bookingDto.getLastName(), font);
           Chunk nightlyPrice = new Chunk("Price Per Night: "+bookingDto.getRoom().getPricePerNight(), font);
           Chunk roomNo = new Chunk("Room No.: "+bookingDto.getRoom().getRoomNo(), font);
           Chunk checkIn = new Chunk("CheckIn Date: "+bookingDto.getCheckIn(), font);
           Chunk checkOut = new Chunk("CheckOut Date: "+bookingDto.getCheckOut(), font);
           Chunk  noPerson= new Chunk("No. of Person: "+bookingDto.getNoPersons(), font);
           Chunk nights = new Chunk("Total Nights: "+bookingDto.getNights(), font);
           Chunk price = new Chunk("Total Price: "+bookingDto.getPrice(), font);

           document.add(bookingConfirmation);
           document.add(new Paragraph("\n"));
           document.add(firstName);
           document.add(new Paragraph("\n"));
           document.add(lastName);
           document.add(new Paragraph("\n"));
           document.add(noPerson);
           document.add(new Paragraph("\n"));
           document.add(nights);
           document.add(new Paragraph("\n"));
           document.add(nightlyPrice);
           document.add(new Paragraph("\n"));
           document.add(roomNo);
           document.add(new Paragraph("\n"));
           document.add(checkIn);
           document.add(new Paragraph("\n"));
           document.add(checkOut);
           document.add(new Paragraph("\n"));
           document.add(price);

           document.close();
           return true;
       }catch (Exception e){
           e.printStackTrace();
       }
        return false;
    }
}
