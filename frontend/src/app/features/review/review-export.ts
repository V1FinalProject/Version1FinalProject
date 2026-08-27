import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { NominationView } from '../../core/models/review.model';

export type ExportFormat = 'CSV' | 'JSON' | 'PDF' | 'XLSX';

const COLUMNS: readonly { readonly header: string; readonly value: (row: NominationView) => string }[] = [
  { header: '#', value: (row) => String(row.id) },
  { header: 'Nominee', value: (row) => row.nomineeName },
  { header: 'Nominee email', value: (row) => row.nomineeEmail },
  { header: 'Office', value: (row) => row.nomineeProfile?.workLocation ?? '' },
  { header: 'Nominator', value: (row) => row.nominatorName },
  { header: 'Nominator email', value: (row) => row.nominatorEmail },
  { header: 'Category', value: (row) => row.category },
  { header: 'Flags', value: (row) => row.flags.map((flag) => flag.tagName).join('; ') },
  { header: 'Status', value: (row) => row.status },
  { header: 'Favourite', value: (row) => (row.favourite ? 'Yes' : 'No') },
  { header: 'Quarter', value: (row) => row.quarter },
  { header: 'Submitted', value: (row) => row.timestamp },
];

/** Table rows for the currently visible nominations, one array of cells per row. */
function toTableRows(rows: readonly NominationView[]): string[][] {
  return rows.map((row) => COLUMNS.map((column) => column.value(row)));
}

/** Triggers a browser download of `content` without navigating away from the page. */
function downloadBlob(content: BlobPart, mimeType: string, filename: string): void {
  const url = URL.createObjectURL(new Blob([content], { type: mimeType }));
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}

function csvCell(value: string): string {
  return /[",\n]/.test(value) ? `"${value.replace(/"/g, '""')}"` : value;
}

function exportCsv(rows: readonly NominationView[], filename: string): void {
  const lines = [COLUMNS.map((column) => column.header), ...toTableRows(rows)].map((line) =>
    line.map(csvCell).join(','),
  );
  downloadBlob(lines.join('\r\n'), 'text/csv;charset=utf-8', `${filename}.csv`);
}

function exportJson(rows: readonly NominationView[], filename: string): void {
  downloadBlob(JSON.stringify(rows, null, 2), 'application/json;charset=utf-8', `${filename}.json`);
}

function exportPdf(rows: readonly NominationView[], filename: string): void {
  const doc = new jsPDF({ orientation: 'landscape' });
  doc.setFontSize(14);
  doc.text('Star Award nominations', 14, 14);
  autoTable(doc, {
    startY: 20,
    head: [COLUMNS.map((column) => column.header)],
    body: toTableRows(rows),
    styles: { fontSize: 7, cellPadding: 2 },
    headStyles: { fillColor: [40, 40, 40] },
  });
  doc.save(`${filename}.pdf`);
}

function exportXlsx(rows: readonly NominationView[], filename: string): void {
  const sheet = XLSX.utils.aoa_to_sheet([COLUMNS.map((column) => column.header), ...toTableRows(rows)]);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, sheet, 'Nominations');
  XLSX.writeFile(workbook, `${filename}.xlsx`);
}

/** Exports the given nominations in `format`, downloading the result as a file. */
export function exportNominations(rows: readonly NominationView[], format: ExportFormat, filename: string): void {
  switch (format) {
    case 'CSV':
      exportCsv(rows, filename);
      return;
    case 'JSON':
      exportJson(rows, filename);
      return;
    case 'PDF':
      exportPdf(rows, filename);
      return;
    case 'XLSX':
      exportXlsx(rows, filename);
      return;
  }
}
