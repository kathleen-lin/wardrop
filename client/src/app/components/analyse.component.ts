import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ItemService } from '../item.service';

@Component({
  selector: 'app-analyse',
  templateUrl: './analyse.component.html',
  styleUrls: ['./analyse.component.css']
})
export class AnalyseComponent implements OnInit{

  fileName!: string

  constructor(private activatedRoute: ActivatedRoute, private itmSvc: ItemService) {}

  ngOnInit(): void {
      this.activatedRoute.params.subscribe(
        async (params) => {
          this.fileName = params['filename']; 
          console.log(this.fileName)
          this.itmSvc.analyseImage(this.fileName)
            .then((result) => console.log(result))
            .then((err) => console.log(err))
        }
          )
  
    }

}
