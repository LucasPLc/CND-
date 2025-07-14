
import React from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface HistoryRecord {
  id: string;
  data: string;
  acao: string;
  situacaoAnterior?: string;
  situacaoNova?: string;
  usuario: string;
  observacoes?: string;
}

interface HistoryModalProps {
  isOpen: boolean;
  onClose: () => void;
  cnpj: string;
  tipo: string;
}

const HistoryModal: React.FC<HistoryModalProps> = ({ isOpen, onClose, cnpj, tipo }) => {
  // Mock history data - replace with actual API call
  const historyData: HistoryRecord[] = [
    {
      id: '1',
      data: '2024-01-16',
      acao: 'Processamento Concluído',
      situacaoAnterior: 'Pendente',
      situacaoNova: 'Negativa',
      usuario: 'Sistema Automático',
      observacoes: 'Processamento automático realizado com sucesso'
    },
    {
      id: '2',
      data: '2024-01-15',
      acao: 'Consulta Iniciada',
      usuario: 'João Silva',
      observacoes: 'Consulta iniciada pelo usuário'
    }
  ];

  const getSituacaoBadgeColor = (situacao: string) => {
    switch (situacao) {
      case 'Negativa': return 'bg-green-100 text-green-800 border-green-200';
      case 'Positiva com Efeitos de Negativa': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'Positiva': return 'bg-red-100 text-red-800 border-red-200';
      case 'Pendente': return 'bg-blue-100 text-blue-800 border-blue-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            Histórico - CNPJ: {cnpj} | Tipo: {tipo}
          </DialogTitle>
        </DialogHeader>
        
        <div className="space-y-4 py-4">
          {historyData.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              Nenhum histórico encontrado para este registro.
            </div>
          ) : (
            historyData.map((item) => (
              <div key={item.id} className="border rounded-lg p-4 space-y-2">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h4 className="font-semibold text-lg">{item.acao}</h4>
                    <p className="text-sm text-gray-600">
                      {format(new Date(item.data), 'dd/MM/yyyy HH:mm', { locale: ptBR })} - {item.usuario}
                    </p>
                  </div>
                </div>
                
                {(item.situacaoAnterior || item.situacaoNova) && (
                  <div className="flex items-center space-x-2">
                    <span className="text-sm">Situação:</span>
                    {item.situacaoAnterior && (
                      <>
                        <Badge className={getSituacaoBadgeColor(item.situacaoAnterior)}>
                          {item.situacaoAnterior}
                        </Badge>
                        <span>→</span>
                      </>
                    )}
                    {item.situacaoNova && (
                      <Badge className={getSituacaoBadgeColor(item.situacaoNova)}>
                        {item.situacaoNova}
                      </Badge>
                    )}
                  </div>
                )}
                
                {item.observacoes && (
                  <div className="text-sm text-gray-700 bg-gray-50 p-2 rounded">
                    <strong>Observações:</strong> {item.observacoes}
                  </div>
                )}
              </div>
            ))
          )}
        </div>

        <div className="flex justify-end pt-4">
          <Button variant="outline" onClick={onClose}>
            Fechar
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default HistoryModal;
