import { Component } from '@angular/core';
import { StockService } from '../stock.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-search',
    standalone: true,
    imports: [CommonModule, FormsModule], // ✅ Add FormsModule here
    templateUrl: './search.component.html',
    styleUrl: './search.component.css'
  })
export class SearchComponent {
  symbol = '';
  loading = false;
  result: any = null;
  error = '';
  objectKeys = Object.keys;


  constructor(private stockService: StockService) {}

  search() {
    if (!this.symbol || this.symbol.trim().length === 0) return;
    this.loading = true;
    this.result = null;
    this.error = '';
    this.stockService.getStock(this.symbol.trim()).subscribe({
      next: (res) => {
        this.result = res;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to fetch data';
        this.loading = false;
      }
    });
  }
}