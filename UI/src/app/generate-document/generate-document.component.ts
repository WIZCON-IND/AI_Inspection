import { Component } from '@angular/core';
import {GenerateService} from './generate.service';

@Component({
  selector: 'app-generate-document',
  imports: [],
  templateUrl: './generate-document.component.html',
  styleUrl: './generate-document.component.scss'
})
export class GenerateDocumentComponent {

  validDownloadButton = false;

  constructor(private generateService: GenerateService) {
  }

  createDoc() {
    this.generateService.createDoc().subscribe((data:any) =>{
      this.validDownloadButton = data.status;
    })

  }

  downloadDoc() {
    this.generateService.downloadDoc().subscribe((response) =>{
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
        const url = window.URL.createObjectURL(blob);

        // Create a link element to trigger the download
        const a = document.createElement('a');
        a.href = url;
        a.download = 'InspectionReport.docx';
        a.click();

        // Cleanup
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Download failed:', error);
      }
    );
  }
}
